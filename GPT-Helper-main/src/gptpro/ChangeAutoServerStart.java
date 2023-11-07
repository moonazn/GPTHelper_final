package gptpro;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ChangeAutoServerStart extends AbstractHandler implements IActionDelegate{
    
	Boolean autostart;
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        
        
        return null;
    }

	@Override
	public void run(IAction action) {
		IWorkbench workbench = PlatformUI.getWorkbench();
	    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        

        // 다이얼로그 표시
        MessageDialog dialog = new MessageDialog(
            window.getShell(),
            "Auto Start Option",
            null,
            "자동으로 h2 DB서버 시작 옵션을 변경합니다.",
            MessageDialog.QUESTION,
            new String[] { "활성화", "비활성화" },
            0
        );
        
        int result = dialog.open();
        
        // 사용자가 선택한 값에 따라 옵션 변경
        if (result == 0) { // "활성화" 선택
        	autostart = true;
        } else if (result == 1) { // "비활성화" 선택
        	autostart = false;
        }
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
