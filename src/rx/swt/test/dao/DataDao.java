package rx.swt.test.dao;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuwenzhe2008@gmail.com
 *
 */
public class DataDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataDao.class);
  
  private static final String[] STATUS_LIST = {"New", "Running", "Done", "Abort"};
  private static final int[] STATUS_LOAD_TIME = {143, 4256, 2427, 3564, 985, 3743, 2753};
  private static final int[] RUN_TIME_LOAD_TIME = {4213, 864, 2352, 3456, 2641, 5023, 1987};
  
  public List<Integer> loadIdList() {
    LOGGER.debug("loadIdList");
    sleep(200); // sleep 200 ms to simulate loading time
    return Arrays.asList(1, 2, 3, 4, 5, 6, 7);
  }
  
  public String loadStatus(int id) {
    LOGGER.debug("loadStatus for id {}", id);
    int loadingTime = STATUS_LOAD_TIME[id - 1];
    sleep(loadingTime); // sleep to simulate loading time
    return STATUS_LIST[loadingTime % STATUS_LIST.length];
  }
  
  public int loadRunTime(int id) {
    LOGGER.debug("loadRunTime for id {}", id);
    int loadTimeInMs = RUN_TIME_LOAD_TIME[id - 1];
    sleep(loadTimeInMs); // sleep to simulate loading time
    return loadTimeInMs;
  }
  
  private static void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
