package rx.swt.test;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import rx.swt.test.ui.MainComposite;

public class View extends ViewPart {
	public static final String ID = "rx.swt.test.view";

	private MainComposite ui;


	class ViewLabelProvider extends LabelProvider  {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		@Override
    public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
  public void createPartControl(Composite parent) {
	  ui = new MainComposite(parent, SWT.NONE);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
  public void setFocus() {
		ui.setFocus();
	}
}