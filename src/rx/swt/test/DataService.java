package rx.swt.test;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.swt.test.dao.DataDao;
import rx.swt.test.model.Data;

/**
 * @author liuwenzhe2008@gmail.com
 *
 */
public class DataService {

  private final DataDao dao = new DataDao();
  
  public List<Data> load() {
    return dao.loadIdList().stream()
        .map(this::loadData)
        .collect(toList());
  }
  
  private Data loadData(int id) {
    Data dt = new Data(id);
    dt.setStatus(dao.loadStatus(id));
    dt.setRunTime(dao.loadRunTime(id));
    return dt;
  }
  
  /**
   * fast load, only has the ID information in Data
   */
  public List<Data> fastLoad() {
    return dao.loadIdList().stream()
        .map(Data::new)
        .collect(toList());
  }
  
  public Observable<Data> createObservableToLoadMoreData(List<Data> datasOnlyWithId) {
    Observable<Data> obsvData = Observable.from(datasOnlyWithId);
    
    Observable<Data> obsvLoadStatus = obsvData.flatMap(data -> 
      Observable.<Data>create(subscriber -> {
        data.setStatus(dao.loadStatus(data.getId()));
        subscriber.onNext(data);
        subscriber.onCompleted();
      })
      .subscribeOn(Schedulers.io())
    );
    
    Observable<Data> obsvLoadRunTime = obsvData.flatMap(data -> 
      Observable.<Data>create(subscriber -> {
        data.setRunTime(dao.loadRunTime(data.getId()));
        subscriber.onNext(data);
        subscriber.onCompleted();
      })
      .subscribeOn(Schedulers.io())
    );
    
    return Observable.<Data>merge(obsvLoadStatus, obsvLoadRunTime)
        .buffer(500, TimeUnit.MILLISECONDS)
        .filter(dataList -> !dataList.isEmpty())
        // remove duplicate and keep latest one
        .flatMap(dataList -> Observable.from(reverse(dataList)).distinct(data -> data.getId()))
        ;
  }
  
  private List<Data> reverse(List<Data> dataList) {
    Data[] result = new Data[dataList.size()];
    int i = dataList.size();
    for (Data dt : dataList) {
      result[--i] = dt;
    }
    return Arrays.asList(result);
  }
}
