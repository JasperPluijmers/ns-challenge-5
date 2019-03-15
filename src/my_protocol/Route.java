package my_protocol;

/**
 * Simple object which describes a route entry in the forwarding table.
 * Can be extended to include additional data.
 *
 * Copyright University of Twente, 2013-2019
 *
 **************************************************************************
 *                            Copyright notice                            *
 *                                                                        *
 *             This file may ONLY be distributed UNMODIFIED.              *
 * In particular, a correct solution to the challenge must NOT be posted  *
 * in public places, to preserve the learning effect for future students. *
 **************************************************************************
 */
public class Route {
    public int nextHop;
    public int cost;

    public Route(int nextHop, int cost) {
        this.nextHop = nextHop;
        this.cost = cost;
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof Route) {
            Route route = (Route) object;

            return route.nextHop == nextHop && route.cost == cost;
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("Route{nextHop: %d, cost: %d}", nextHop, cost);
    }
}