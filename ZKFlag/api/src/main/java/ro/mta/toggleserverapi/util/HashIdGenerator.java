package ro.mta.toggleserverapi.util;

import org.hashids.Hashids;

public class HashIdGenerator {
        public static void main(String[] args) {
            Hashids hashids = new Hashids("context-salt", 8);

            System.out.println("ID 1 -> " + hashids.encode(1));
            System.out.println("ID 4 -> " + hashids.encode(2));
            System.out.println("ID 5 -> " + hashids.encode(3));
        }
    }

