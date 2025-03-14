package ro.mta.toggleserverapi.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ListUtil {
    public static Long listSize(List<?> list){
        return Optional.ofNullable(list)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .count();
    }
}
