package gptpro;

import java.util.HashMap;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.ITextEditor;


public class MarkerManager {
    // Store memo data, indexed by line number
    private static HashMap<Integer, String> lineToMemoMap = new HashMap<>();

    // Save a memo for a specific line number
    public static void saveMemo(int line, String memo) {
        lineToMemoMap.put(line, memo);
    }

    // Load a memo for a specific line number
    public static String loadMemo(int line) {
        return lineToMemoMap.getOrDefault(line, "No memo available.");
    }

    public static void createOrUpdateMarker(ITextEditor editor, int line, String memo) {
        if (editor != null) {
            IResource resource = (IResource) editor.getEditorInput().getAdapter(IResource.class);
            if (resource != null) {
                try {
                    // Create a new marker or update existing marker
                    IMarker[] markers = resource.findMarkers(IMarker.BOOKMARK, true, IResource.DEPTH_ZERO);
                    for (IMarker marker : markers) {
                        if (marker.getAttribute(IMarker.LINE_NUMBER, -1) == line) {
                            // Update the memo for this line
                            marker.setAttribute(IMarker.MESSAGE, memo);
                            saveMemo(line, memo);
                            return;
                        }
                    }

                    // Create a new marker if it doesn't exist
                    IMarker marker = resource.createMarker(IMarker.BOOKMARK);
                    marker.setAttribute(IMarker.LINE_NUMBER, line);
                    marker.setAttribute(IMarker.MESSAGE, memo);

                    // Save the memo for this line
                    saveMemo(line, memo);
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void deleteMarker(ITextEditor editor, int line) {
        if (editor != null) {
            IResource resource = (IResource) editor.getEditorInput().getAdapter(IResource.class);
            if (resource != null) {
                try {
                    // Find and delete the marker
                    IMarker[] markers = resource.findMarkers(IMarker.BOOKMARK, true, IResource.DEPTH_ZERO);
                    for (IMarker marker : markers) {
                        if (marker.getAttribute(IMarker.LINE_NUMBER, -1) == line) {
                            marker.delete();
                            // Delete the memo for this line
                            lineToMemoMap.remove(line);
                            break;
                        }
                    }
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void showPopup(ITextEditor editor, int line) {
        String memo = loadMemo(line);
        Display.getDefault().asyncExec(() -> {
            MessageDialog.openInformation(editor.getSite().getShell(), "Memo", memo);
        });
    }
    
  
        
   

}