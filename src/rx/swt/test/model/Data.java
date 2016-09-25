package rx.swt.test.model;

/**
 * Data class, may be shared in multiple threads, it should be thread safe.
 * 
 * @author liuwenzhe2008@gmail.com
 *
 */
public class Data {

  private final int id;
  
  private volatile String status = "";
  
  private volatile int runTime;
  
  public Data(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getRunTime() {
    return runTime;
  }

  public void setRunTime(int runTime) {
    this.runTime = runTime;
  }

  @Override
  public String toString() {
    return "Data [id=" + id + ", status=" + status + ", runTime=" + runTime + "]";
  } 
}
