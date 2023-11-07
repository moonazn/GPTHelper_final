package gptpro;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


public class ErrorHelperAction extends Action implements IActionDelegate {

	private String response;
	
	private Button translationButton;

	@Override
	public void run(IAction action) {
		System.out.println("Action run called");
	    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	    if (window != null) {
	        IMarker marker = getSelectedMarker();
	        
	        if (marker != null) {
	            
	            try {
	                String errorMessage = (String) marker.getAttribute(IMarker.MESSAGE);
	                String escapedCode = OpenAIHandler.escapeJson(errorMessage);
		        	
		        	String selectedText = "{\"model\": \"gpt-3.5-turbo\", \"messages\": "
		              		+ "[{\"role\": \"system\", \"content\": \"Error message: \"},"
		              		+ "{\"role\": \"user\", \"content\": \"code: " + escapedCode
		              		+ "\"}], \"max_tokens\": 500}";
		            
		            OpenAIHandler handler = new OpenAIHandler();
		            
	                System.out.println("Error message: " + errorMessage);
	                
	                try {
	                    response = (String) handler.callOpenAPI(selectedText); 
	                } catch (ExecutionException ex) {
	                    ex.printStackTrace();
	                }
	             // UI 스레드에서 다이얼로그를 열기 위해 asyncExec 사용
	                Display.getDefault().asyncExec(() -> {
	                	openNewWindow2();
	                });
	            } catch (CoreException e) {
	                e.printStackTrace();
	            }
	        }
	    }
		
	}
	
    public ErrorHelperAction() {
        setText("Error Helper");
        setToolTipText("Provide help for this error");
    }

    private IMarker getSelectedMarker() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            ISelectionService selectionService = window.getSelectionService();
            IStructuredSelection selection = (IStructuredSelection) selectionService.getSelection("org.eclipse.ui.views.ProblemView");
            if (selection != null && !selection.isEmpty()) {
                Object selectedObject = selection.getFirstElement();
                if (selectedObject instanceof org.eclipse.ui.views.markers.MarkerItem) {
                    org.eclipse.ui.views.markers.MarkerItem markerItem = (org.eclipse.ui.views.markers.MarkerItem) selectedObject;
                    IMarker marker = markerItem.getMarker();
                    return marker;
                }
            }
        }
        return null;
    }



	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		System.out.println("selectionChanged called");
	    if (selection instanceof IStructuredSelection) {
	        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
	        if (!structuredSelection.isEmpty()) {
	            Object selectedObject = structuredSelection.getFirstElement();
	            if (selectedObject instanceof IMarker) {
	                System.out.println("Selected marker found");
	            }
	        }
	    }
		
	}
	
	private void openNewWindow2() {
        Shell newShell2 = new Shell(Display.getDefault());
        newShell2.setText("ERROR");
        newShell2.setLayout(new GridLayout(1, false));

        // 다른 곳에서 받아온 정보를 출력할 Label 생성
        Label infoLabel = new Label(newShell2, SWT.WRAP);
        infoLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        // 다른 곳에서 받아온 정보를 출력하기 위해 해당 정보를 변수에 저장한 뒤 setText() 메서드로 Label에 설정
        

        
        if (response != null) {
            Display.getDefault().asyncExec(() -> {
                if (infoLabel != null && !infoLabel.isDisposed()) {
                    System.out.print("yes");
                    infoLabel.setText(response);
                }
            });
        }
          

        translationButton = new Button(newShell2, SWT.PUSH);
        translationButton.setText("TRANSLATE");
        GridData buttonData4 = new GridData(SWT.CENTER, SWT.BEGINNING, false, false);
        translationButton.setLayoutData(buttonData4);
        
        translationButton.addListener(SWT.Selection, e -> {
        	String escapedText = OpenAIHandler.escapeJson(infoLabel.getText());
        	String selectedText = "{\"model\": \"gpt-3.5-turbo\", \"messages\": "
              		+ "[{\"role\": \"system\", \"content\": \"Translate into Korean.\"},"
              		+ "{\"role\": \"user\", \"content\": \" " + escapedText
              		+ "\"}], \"max_tokens\": 500}";
        	
        	OpenAIHandler handler = new OpenAIHandler();

            try {
                String reponse = (String) handler.callOpenAPI(selectedText); 
                if (reponse != null) {
                    Display.getDefault().asyncExec(() -> {
                        if (infoLabel != null && !infoLabel.isDisposed()) {
                        	System.out.print("yes");
                            infoLabel.setText(reponse);
                        }
                    });
                }
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
          });
        
        newShell2.setSize(500, 600);
        newShell2.open();
    }

}