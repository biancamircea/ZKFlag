package ro.mta.toggleserverapi.util;

import org.hashids.Hashids;

public class HashIdGenerator {
        public static void main(String[] args) {
            Hashids hashids = new Hashids("user-salt", 8);

            System.out.println("ID 1 -> " + hashids.encode(1));

        }
    }

