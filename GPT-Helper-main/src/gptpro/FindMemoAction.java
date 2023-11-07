package gptpro;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.window.Window;

public class FindMemoAction extends AbstractHandler implements IActionDelegate {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        run(null);
        return null;
    }

    public void run(IAction action) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        // Open FindMemoDialog
        FindMemoDialog dialog = new FindMemoDialog(window.getShell());
        int result = dialog.open();

        if (result == Window.OK) {
            java.util.List<MemoDTO> foundMemos = dialog.getFoundMemos();
            if (foundMemos == null || foundMemos.isEmpty()) {
                MessageDialog.openError(window.getShell(), "Error", "No matching memo found.");
            } else {
                for (MemoDTO foundMemo : foundMemos) {
                    MemoPopupDialog popupDialog = new MemoPopupDialog(window.getShell(), foundMemo.getLine(), null);
                    popupDialog.open();
                }
            }
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        // This method is called when the selection changes in the workbench
    }

    @Override
    public void dispose() {
        // Release resources
    }
}