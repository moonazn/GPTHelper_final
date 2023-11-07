package gptpro;

public class MemoDTO {
    private int line;
    private String content;
    private boolean hasIcon;

    public MemoDTO(int line, String content) {
        this.line = line;
        this.content = content;
        this.hasIcon = !content.isEmpty();  // Content가 있는 경우에만 아이콘 표시
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.hasIcon = !content.isEmpty();
    }

    public int getLine() {
        return line;
    }

    public boolean hasIcon() {
        return hasIcon;
    }

    public void setHasIcon(boolean hasIcon) {
        this.hasIcon = hasIcon;
    }
}
