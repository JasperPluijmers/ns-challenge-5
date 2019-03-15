package my_protocol;

import framework.*;

import java.util.*;

/**
 * @version 12-03-2019
 * <p>
 * Copyright University of Twente, 2013-2019
 * <p>
 * *************************************************************************
 * Copyright notice                            *
 * *
 * This file may ONLY be distributed UNMODIFIED.              *
 * In particular, a correct solution to the challenge must NOT be posted  *
 * in public places, to preserve the learning effect for future students. *
 * *************************************************************************
 */
public class RoutingProtocol implements IRoutingProtocol {
    private LinkLayer linkLayer;

    // You can use this data structure to store your routing table.
    private HashMap<Integer, Route> myRoutingTable = new HashMap<>();
    private DataTable neighboursTable = new DataTable(N_ROUTERS);
    private static final int N_ROUTERS = 6;
    private static int myAddress;
    private static final int BOUND_INFINITY = 100000;

    @Override
    public void init(LinkLayer linkLayer) {
        this.linkLayer = linkLayer;

        neighboursTable.set(N_ROUTERS - 1, N_ROUTERS - 1, 0);

        Packet initialBroadcast = new Packet(this.linkLayer.getOwnAddress(), 0, neighboursTable);
        linkLayer.transmit(initialBroadcast);
        this.myAddress = this.linkLayer.getOwnAddress();
    }

    private void updateTable(Packet packet, int neighbour) {
        neighboursTable.setRow(neighbour - 1, packet.getDataTable().getRow(neighbour - 1));
    }

    private void updateRoutes(Set<Integer> currentNeighbours) {
        //loop over all destinations
        for (int destination = 1; destination < N_ROUTERS + 1; destination++) {
            //initial values
            int lowestCost = myRoutingTable.containsKey(destination) ? myRoutingTable.get(destination).cost : BOUND_INFINITY;
            int nextHop = 0;
            boolean ischanged = false;

            //never find a route to my own address
            if (destination == myAddress) {
                continue;
            }

            //Only check for nodes that are actually our neighbours
            for (int neighbour : currentNeighbours) {
                //lookup the cost from the neighbour to the destination, then calculate our cost to it
                int costFromNeighbour = neighboursTable.get(neighbour - 1, destination - 1);
                int costFromMe = costFromNeighbour + neighboursTable.get(myAddress - 1, neighbour - 1);
                //0 is never a real cost
                if (costFromNeighbour != 0) {
                    //If we don't have a route to the destination we should always use a route
                    if (!myRoutingTable.containsKey(destination)) {
                        lowestCost = costFromMe;
                        nextHop = neighbour;
                        myRoutingTable.put(destination, new Route(nextHop, lowestCost));
                        ischanged = true;
                    }
                    //Otherwise check if we found a better route
                    if (costFromMe < lowestCost) {
                        lowestCost = costFromMe;
                        nextHop = neighbour;
                        ischanged = true;
                    }
                }
            }
            //If we found a new route update it.
            if (ischanged) {
                myRoutingTable.put(destination, new Route(nextHop, lowestCost));
                neighboursTable.set(myAddress - 1, destination - 1, lowestCost);
            }

        }
    }


    @Override
    public void tick(PacketWithLinkCost[] packetsWithLinkCosts) {
        // Get the address of this node

        System.out.println("tick; received " + packetsWithLinkCosts.length + " packets");

        Set<Integer> currentNeighbours = new HashSet<>();

        // first process the incoming packets; loop over them:
        for (PacketWithLinkCost packetWithLinkCost : packetsWithLinkCosts) {
            // Update routing table for direct neighbours
            Packet packet = packetWithLinkCost.getPacket();
            int neighbour = packet.getSourceAddress();             // from whom is the packet?
            int linkcost = packetWithLinkCost.getLinkCost();  // what's the link cost from/to this neighbour?

            updateTable(packet, neighbour);
            currentNeighbours.add(neighbour);

            if (!myRoutingTable.containsKey(neighbour)) {
                myRoutingTable.put(neighbour, new Route(neighbour, linkcost));
                neighboursTable.set(myAddress - 1, neighbour - 1, linkcost);
            }
        }
        updateRoutes(currentNeighbours);

        Packet initialBroadcast = new Packet(this.linkLayer.getOwnAddress(), 0, neighboursTable);
        linkLayer.transmit(initialBroadcast);
    }

    private void printTable() {
        for (int destination = 1; destination < N_ROUTERS + 1; destination++) {
            System.out.println(Arrays.toString(neighboursTable.getRow(destination - 1)));
        }
    }

    public Map<Integer, Integer> getForwardingTable() {
        // This code extracts from your routing table the forwarding table.
        // The result of this method is send to the server to validate and score your protocol.

        // <Destination, NextHop>
        HashMap<Integer, Integer> ft = new HashMap<>();

        for (Map.Entry<Integer, Route> entry : myRoutingTable.entrySet()) {
            ft.put(entry.getKey(), entry.getValue().nextHop);
        }

        return ft;
    }
}