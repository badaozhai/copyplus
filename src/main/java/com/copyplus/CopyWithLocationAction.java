package com.copyplus;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.datatransfer.StringSelection;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CopyWithLocationAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (editor == null || file == null) {
            return;
        }

        SelectionModel selection = editor.getSelectionModel();
        String selectedText = selection.getSelectedText();
        if (selectedText == null || selectedText.isEmpty()) {
            return;
        }

        Document document = editor.getDocument();
        int startLine = document.getLineNumber(selection.getSelectionStart()) + 1;
        int endOffset = selection.getSelectionEnd();
        // 行选模式末尾会包含 \n 指向下一行起始；若选中以换行结尾，结束行号应回退一行
        int rawEndLine = document.getLineNumber(endOffset) + 1;
        int endLine = (selectedText.endsWith("\n") && rawEndLine > startLine) ? rawEndLine - 1 : rawEndLine;

        String normalized = stripTrailingNewline(selectedText);
        boolean multiLine = startLine != endLine;
        String body = multiLine ? dedent(normalized) : normalized;

        StringBuilder sb = new StringBuilder();
        sb.append("请帮我分析下面这段内容：\n\n");
        sb.append("文件: ").append(file.getName()).append('\n');
        sb.append("绝对路径: ").append(file.getPath()).append('\n');
        String relativePath = computeRelativePath(project, file);
        if (relativePath != null) {
            sb.append("项目内路径: ").append(relativePath).append('\n');
        }
        if (multiLine) {
            sb.append("行号: ").append(startLine).append('-').append(endLine)
                    .append(" (共 ").append(endLine - startLine + 1).append(" 行)\n");
        } else {
            sb.append("行号: ").append(startLine).append('\n');
        }
        sb.append('\n');
        sb.append(body).append('\n');

        CopyPasteManager.getInstance().setContents(new StringSelection(sb.toString()));
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        boolean hasSelection = editor != null && editor.getSelectionModel().hasSelection();
        e.getPresentation().setEnabledAndVisible(hasSelection);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Nullable
    private static String computeRelativePath(@Nullable Project project, VirtualFile file) {
        if (project == null) return null;
        String basePath = project.getBasePath();
        if (basePath == null) return null;
        try {
            Path base = Paths.get(basePath).toAbsolutePath().normalize();
            Path target = Paths.get(file.getPath()).toAbsolutePath().normalize();
            if (!target.startsWith(base)) return null;
            String rel = base.relativize(target).toString();
            return rel.replace('\\', '/');
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String stripTrailingNewline(String s) {
        if (s.endsWith("\r\n")) return s.substring(0, s.length() - 2);
        if (s.endsWith("\n") || s.endsWith("\r")) return s.substring(0, s.length() - 1);
        return s;
    }

    private static String dedent(String text) {
        String[] lines = text.split("\n", -1);
        int minIndent = Integer.MAX_VALUE;
        for (String line : lines) {
            if (line.isBlank()) continue;
            int i = 0;
            while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
                i++;
            }
            if (i < minIndent) minIndent = i;
            if (minIndent == 0) break;
        }
        if (minIndent == Integer.MAX_VALUE || minIndent == 0) {
            return text;
        }
        StringBuilder out = new StringBuilder(text.length());
        for (int idx = 0; idx < lines.length; idx++) {
            String line = lines[idx];
            if (line.isBlank()) {
                out.append(line);
            } else {
                out.append(line.substring(Math.min(minIndent, line.length())));
            }
            if (idx < lines.length - 1) out.append('\n');
        }
        return out.toString();
    }
}
