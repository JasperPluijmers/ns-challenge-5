package framework;

/*
 *                                                          *
 **                 DO NOT EDIT THIS FILE                  **
 ***         YOU DO NOT HAVE TO LOOK IN THIS FILE         ***
 **** IF YOU HAVE QUESTIONS PLEASE DO ASK A TA FOR HELP  ****
 *****                                                  *****
 ************************************************************
 */

/**
 * A packet with the cost of the link over which the packet was received
 *
 * @author Frans van Dijk, University of Twente.
 * @version 12-03-2019
 */
public class PacketWithLinkCost {

    private final Packet packet;
    private final int linkCost;

    PacketWithLinkCost(Packet packet, int linkCost) {
        this.packet = packet;
        this.linkCost = linkCost;
    }

    /**
     * Gets the received {@link Packet}.
     *
     * @return the received {@link Packet}
     */
    public Packet getPacket() {
        return packet;
    }

    /**
     * Gets the cost of the link over which the packet was received.
     *
     * @return the cost of the link over which the packet was received
     */
    public int getLinkCost() {
        return linkCost;
    }
}
