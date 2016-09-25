package rx.swt.test.ui;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zakgof.rxswt.SwtScheduler;

import rx.swt.test.DataService;
import rx.swt.test.model.Data;

/**
 * @author liuwenzhe2008@gmail.com
 *
 */
public class MainComposite extends Composite {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(MainComposite.class);
  
  private final TableViewer tableViewer;
  private final DataService service = new DataService();

  /**
   * Create the composite.
   * @param parent
   * @param style
   */
  public MainComposite(Composite parent, int style) {
    super(parent, style);
    setLayout(new GridLayout(3, false));
    
    tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
    Table table = tableViewer.getTable();
    GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
    gd_table.widthHint = 344;
    table.setLayoutData(gd_table);
    table.setLinesVisible(true);
    table.setHeaderVisible(true);

    tableViewer.setContentProvider(new ArrayContentProvider());
    
    createIdCol();
    createStatusCol();
    createRunTimeCol();
    
    createSyncLoadButton();
    createAsyncLoadButton();
    createClearButton();
  }

  private void createRunTimeCol() {
    TableViewerColumn tvc = new TableViewerColumn(tableViewer, SWT.NONE);
    TableColumn tblclmnRunTime = tvc.getColumn();
    tblclmnRunTime.setWidth(100);
    tblclmnRunTime.setText("Run time");
    
    tvc.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        return Integer.toString(((Data) element).getRunTime());
      }
    });
  }

  private void createStatusCol() {
    TableViewerColumn tvc = new TableViewerColumn(tableViewer, SWT.NONE);
    TableColumn tblclmnStatus = tvc.getColumn();
    tblclmnStatus.setWidth(100);
    tblclmnStatus.setText("Status");
    
    tvc.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        return ((Data) element).getStatus();
      }
    });
  }

  private void createIdCol() {
    TableViewerColumn tvc = new TableViewerColumn(tableViewer, SWT.NONE);
    TableColumn tblclmn = tvc.getColumn();
    tblclmn.setWidth(100);
    tblclmn.setText("ID");
    
    tvc.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        return Integer.toString(((Data) element).getId());
      }
    });
  }

  @Override
  protected void checkSubclass() {
    // Disable the check that prevents subclassing of SWT components
  }
  
  private void createClearButton() {
    Button btnClear = new Button(this, SWT.NONE);
    btnClear.setText("Clear");
    
    btnClear.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        tableViewer.setInput(new ArrayList<Data>());
      }
    });
  }
  
  private void createSyncLoadButton() {
    Button btnSyncLoad = new Button(this, SWT.NONE);
    btnSyncLoad.setText("Sync Load");
    
    btnSyncLoad.addSelectionListener(new SelectionAdapter() {
      
      @Override
      public void widgetSelected(SelectionEvent e) {
        long startTime = System.currentTimeMillis();
        BusyIndicator.showWhile(getDisplay(), () -> {
          List<Data> datas = service.load();
          tableViewer.setInput(datas);
          showDoneMessage(startTime);
        });
      }
    });
  }

  private void createAsyncLoadButton() {
    Button btnAsyncLoad = new Button(this, SWT.NONE);
    btnAsyncLoad.setText("Async Load");

    btnAsyncLoad.addSelectionListener(new SelectionAdapter() {
      
      @Override
      public void widgetSelected(SelectionEvent e) {
        long startTime = System.currentTimeMillis();
        
        List<Data> datasOnlyWithId = service.fastLoad();
        tableViewer.setInput(datasOnlyWithId);

        service.createObservableToLoadMoreData(datasOnlyWithId)
        .observeOn(SwtScheduler.getInstance())
        .subscribe(data -> {
          LOGGER.debug(data.toString());
          tableViewer.refresh(data);
        }, ex -> {
          LOGGER.error(ex.getMessage(), ex);
          MessageDialog.openError(getShell(), "Error", ex.getMessage());
        }, () -> {
          showDoneMessage(startTime);
        });

      }
    });
  }

  private void showDoneMessage(long startTime) {
    String msg = MessageFormat.format(
        "Success to load all the data, total time is {0} s", 
        (System.currentTimeMillis() - startTime) / 1000.0);
    LOGGER.debug(msg);
    MessageDialog.openInformation(getShell(), "Information", msg);
  }
}
