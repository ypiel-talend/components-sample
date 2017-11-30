package org.talend.components.widget.source;

import java.io.File;
import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Proposable;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Code;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.configuration.ui.widget.TextArea;

@GridLayout({
    @GridLayout.Row({ "connection" }),
    @GridLayout.Row({ "file" }),
    @GridLayout.Row({ "text" }),
    @GridLayout.Row({ "textArea" }),
    @GridLayout.Row({ "code" }),
    @GridLayout.Row({ "checkBox" }),
    @GridLayout.Row({ "dropDownList" }),
    @GridLayout.Row({ "credential" })
})
public class WidgetInputMapperConfiguration implements Serializable {

    @Option
    private ConnectionConfiguration connection;
    
    @Option
    private File file;

    @Option
    private String text;
    
    @Option
    @TextArea
    private String textArea;
    
    @Option
    @Code("Java")
    private String code;
    
    @Option
    private boolean checkBox;
    
    @Option
    @Proposable("list") // TODO add corresponding @DynamicValues
    private Item dropDownList;
    
    @Option
    @Credential
    private String credential;

    
    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    public WidgetInputMapperConfiguration setFile(File file) {
        this.file = file;
        return this;
    }
    
    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    public WidgetInputMapperConfiguration setText(String text) {
        this.text = text;
        return this;
    }
    
    /**
     * @return the textArea
     */
    public String getTextArea() {
        return textArea;
    }

    public WidgetInputMapperConfiguration setTextArea(String textArea) {
        this.textArea = textArea;
        return this;
    }
    
    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    public WidgetInputMapperConfiguration setCode(String code) {
        this.code = code;
        return this;
    }
    
    /**
     * @return the checkBox
     */
    public boolean isCheckBox() {
        return checkBox;
    }

    public WidgetInputMapperConfiguration setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
        return this;
    }
    
    /**
     * @return the dropDownList
     */
    public Item getDropDownList() {
        return dropDownList;
    }

    public WidgetInputMapperConfiguration setDropDownList(Item dropDownList) {
        this.dropDownList = dropDownList;
        return this;
    }
    
    /**
     * @return the credential
     */
    public String getCredential() {
        return credential;
    }
    
    public WidgetInputMapperConfiguration setCredential(String credential) {
        this.credential = credential;
        return this;
    }

    public ConnectionConfiguration getConnection() {
        return connection;
    }

    public WidgetInputMapperConfiguration setConnection(ConnectionConfiguration Connection) {
        this.connection = connection;
        return this;
    }
    
    public static enum Item {
        ITEM1,
        ITEM2,
        ITEM3
    }

}