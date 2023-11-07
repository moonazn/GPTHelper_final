package gptpro;

import org.eclipse.jface.dialogs.Dialog;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.StyledText;
import java.util.ArrayList;
import java.util.List;


public class FindMemoDialog extends Dialog {

   int lineNumber;
    private Text conditionText;
    private TableViewer memoListTableViewer;
    private List<MemoDTO> searchResults;

    AddEditMemoAction addEditMemoAction = new AddEditMemoAction();

    public FindMemoDialog(Shell parentShell) {
        super(parentShell);
    }

    public int open() {
        int result = super.open();
        Shell shell = getShell();
        if (shell != null) {
            shell.setSize(600, 450); // 세로 크기를 조정
        }
        return result;
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Find Memo");
    }

    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginHeight = 10;
        gridLayout.marginWidth = 10;
        composite.setLayout(gridLayout);

        conditionText = new Text(composite, SWT.BORDER);
        GridData conditionTextLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        conditionTextLayoutData.widthHint = 500;
        conditionTextLayoutData.heightHint = 30;
        conditionText.setLayoutData(conditionTextLayoutData);

        Button findButton = new Button(composite, SWT.PUSH);
        findButton.setText("Find");

        GridData findButtonLayoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        findButtonLayoutData.heightHint = 30;
        findButtonLayoutData.widthHint = 60;
        findButton.setLayoutData(findButtonLayoutData);

        findButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String condition = conditionText.getText();
                searchResults = findMemosByCondition(condition);
                memoListTableViewer.setInput(searchResults);
            }
        });

        Table table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
        GridData tableLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        tableLayoutData.widthHint = 500;
        tableLayoutData.heightHint = 300; // 테이블 높이를 조정
        table.setLayoutData(tableLayoutData);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        memoListTableViewer = new TableViewer(table);
        memoListTableViewer.setContentProvider(ArrayContentProvider.getInstance());

        // 라인 번호 컬럼 추가
        TableViewerColumn lineNumberColumn = new TableViewerColumn(memoListTableViewer, SWT.NONE);
        TableColumn lineColumn = lineNumberColumn.getColumn();
        lineColumn.setText("라인 번호");
        lineColumn.setWidth(100); // 라인 번호 컬럼 너비 조정
        lineNumberColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof MemoDTO) {
                    System.out.println(String.valueOf(((MemoDTO) element).getLine()));

                    return String.valueOf(((MemoDTO) element).getLine());
                    
                }
                return super.getText(element);
            }
        });

        // 메모 내용 컬럼 추가
        TableViewerColumn contentColumn = new TableViewerColumn(memoListTableViewer, SWT.NONE);
        TableColumn contentTableColumn = contentColumn.getColumn();
        contentTableColumn.setText("메모 내용");
        contentTableColumn.setWidth(350); // 메모 내용 컬럼 너비 조정
        contentColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof MemoDTO) {
                    return ((MemoDTO) element).getContent();
                }
                return super.getText(element);
            }

            @Override
            public void update(ViewerCell cell) {
                MemoDTO memo = (MemoDTO) cell.getElement();
                String text = getText(memo);
                cell.setText(text);
            }
        });

        // 컬럼 정렬 설정
        ViewerComparator comparator = new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                // MemoDTO 객체를 가져옵니다.
                MemoDTO memo1 = (MemoDTO) e1;
                MemoDTO memo2 = (MemoDTO) e2;

                // 여기에서 원하는 정렬 기준을 설정합니다.
                // 예를 들어, 라인 번호를 기준으로 오름차순 정렬하려면 다음과 같이 설정할 수 있습니다.
                return Integer.compare(memo1.getLine(), memo2.getLine());
            }
        };
        memoListTableViewer.setComparator(comparator);

     // TableViewer에 SelectionChangedListener 추가
        memoListTableViewer.addSelectionChangedListener(event -> {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            if (!selection.isEmpty()) {
                MemoDTO selectedMemo = (MemoDTO) selection.getFirstElement();
                if (selectedMemo != null) {
//                    openMemo(selectedMemo.getLine(), textArea);
                   ITextEditor textEditor = getActiveTextEditor(); // Get the active text editor

                   goToLine(textEditor, selectedMemo.getLine());
                }
            }
        });




        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // Do not create OK and Cancel buttons
    }

    private List<MemoDTO> findMemosByCondition(String condition) {
        MemoManager memoManager = new MemoManager();
        List<MemoDTO> matchingMemos = new ArrayList<>();
        List<MemoDTO> memoList = memoManager.findMemosByCondition(conditionText.getText());
        for (MemoDTO memo : memoList) {
            if (memo.getContent().contains(condition)) {
                matchingMemos.add(memo);
            }
        }
        return matchingMemos;
    }

    public static void goToLine(ITextEditor textEditor, int lineNumber) {
        if (textEditor instanceof AbstractTextEditor) {
            AbstractTextEditor abstractTextEditor = (AbstractTextEditor) textEditor;
            ISelection selection = new TextSelection(lineNumber - 1, 0);
            abstractTextEditor.getSelectionProvider().setSelection(selection);
            StyledText styledText = getStyledText(abstractTextEditor);
            if (styledText != null) {
                int offset = styledText.getOffsetAtLine(lineNumber - 1);
                styledText.setCaretOffset(offset);
                styledText.showSelection();
            }
        }
    }

    private static StyledText getStyledText(AbstractTextEditor textEditor) {
        Control control = textEditor.getAdapter(Control.class);
        if (control instanceof StyledText) {
            return (StyledText) control;
        }
        return null;
    }



    public List<MemoDTO> getFoundMemos() {
        return searchResults;
    }
    
    private ITextEditor getActiveTextEditor() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        if (page != null) {
            IEditorPart editorPart = page.getActiveEditor();

            if (editorPart instanceof ITextEditor) {
                return (ITextEditor) editorPart;
            }
        }

        return null;
    }

}