package gptpro;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

public class Copy implements IHandler {

   @Override
   public void addHandlerListener(IHandlerListener handlerListener) {
      // TODO Auto-generated method stub

   }

   @Override
   public void dispose() {
      // TODO Auto-generated method stub

   }

    // execute() 메서드 수정
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Display display = Display.getDefault();
        String fullTextToCopy = CreateDraw.responseText.getText();
        String extractedText = extractTextBetweenJava(fullTextToCopy);
        
        Clipboard clipboard = new Clipboard(display);
        TextTransfer textTransfer = TextTransfer.getInstance();
        clipboard.setContents(new Object[] { extractedText }, new Transfer[] { textTransfer });
        clipboard.dispose();

     // Practice.copyCommand 핸들러 실행
        try {
            IWorkbench workbench = PlatformUI.getWorkbench();
            ICommandService commandService = workbench.getService(ICommandService.class);
            Command command = commandService.getCommand("gptpro.copyCommand");
            command.executeWithChecks(event);
        } catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    // 코드 부분만 copy 되도록
    public static String extractTextBetweenJava(String input) {
        String extractedText = "";
        Pattern pattern = Pattern.compile("```java(.*?)```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            extractedText = matcher.group(1);
        } else {
            extractedText = input;
        }

        return extractedText;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return true;
    }

    // isHandled() 메서드 추가
    @Override
    public boolean isHandled() {
        return true;
    }

    @Override
    public void removeHandlerListener(IHandlerListener handlerListener) {
        // TODO Auto-generated method stub
    }
}