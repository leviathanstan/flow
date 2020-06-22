package com.rdc.zrj.flow.hystrixcommand;

import org.junit.Test;
import rx.Observable;

import java.util.Iterator;

/**
 * @author leviathanstan
 * @date 05/06/2020 11:21
 */
public class ObservableCommandTest {

    @Test
    public void testObservable() {
        Observable<String> observable= new MyObservableCommand("World").toObservable();

        Iterator<String> iterator = observable.toBlocking().getIterator();
        while(iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    @Test
    public void testToObservable() {
        Observable<String> observable= new MyObservableCommand("World").observe();
        Iterator<String> iterator = observable.toBlocking().getIterator();
        while(iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
