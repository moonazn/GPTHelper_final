package gptpro;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;


public class ToolbarHandler implements IHandler {

   @Override
   public void addHandlerListener(IHandlerListener handlerListener) {
      // TODO Auto-generated method stub

   }

   @Override
   public void dispose() {
      // TODO Auto-generated method stub

   }

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      
      IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

        try {
            // 현재 Workbench 창에서 작업 중인 페이지를 가져옵니다.
            IWorkbenchPage page = window.getActivePage();

            // ViewPart의 ID를 지정하여 해당 View를 엽니다.
            page.showView("gptpro.view1");

            // 기타 View 열기 후 작업을 수행할 수 있습니다.
        } catch (PartInitException e) {
            e.printStackTrace();
        }
 
      return null;
   }

   @Override
   public boolean isEnabled() {
      // TODO Auto-generated method stub
      return true;
   }

   @Override
   public boolean isHandled() {
      // TODO Auto-generated method stub
      return true;
   }

   @Override
   public void removeHandlerListener(IHandlerListener handlerListener) {
      // TODO Auto-generated method stub

   }
}