package gptpro;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ErrorHelper implements IObjectActionDelegate {
    private IJavaElement selectedElement;

    @Override
    public void run(IAction action) {
        // 메뉴 항목이 선택되었을 때 동작하는 로직을 구현합니다.
        if (selectedElement != null) {
            // 선택한 Java 요소를 분석하거나 특정 작업을 수행합니다.
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            Object firstElement = ((IStructuredSelection) selection).getFirstElement();
            if (firstElement instanceof IAdaptable) {
                selectedElement = ((IAdaptable) firstElement).getAdapter(IJavaElement.class);
            }
        }
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        // 필요한 경우 사용합니다.
    }
}