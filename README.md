# CUBA Lookup Preview Add-on

## Usage notes

### Lookup screen template

Susbstitute strings between curly braces (`{}`) with your elements/attributes.
```xml
<window xmlns="http://schemas.haulmont.com/cuba/screen/window.xsd"
        caption="msg://browseCaption"
        focusComponent="table"
        messagesPack="{messagesPack}">
    <data>
        <collection id="{collectionId}"
                    class="{entityClass}"
                    view="{view}">
            <loader id="{loaderId}">
                <query>
                    <![CDATA[{jpql}]]>
                </query>
            </loader>
        </collection>
    </data>
    <dialogMode height="600" width="800"/>
    <layout>
        <split id="split"
               height="100%"
               width="100%"
               pos="25"
               orientation="horizontal"
               dockable="true"
               dockMode="RIGHT"
               reversePosition="true"
               settingsEnabled="true">
            <vbox id="lookupBox" expand="table" height="100%" margin="false,true,false,false"
                  spacing="true">
                <filter id="filter" applyTo="table" dataLoader="{loaderId}">
                    <properties include=".*"/>
                </filter>
                <groupTable id="table"
                            width="100%"
                            dataContainer="{collectionId}">
                    <actions>
                        <action id="create" type="create"/>
                        <action id="edit" type="edit"/>
                        <action id="remove" type="remove"/>
                    </actions>
                    <columns>
                        {columns}
                    </columns>
                    <rowsCount/>
                    <buttonsPanel id="buttonsPanel"
                                  alwaysVisible="true">
                        <button id="createBtn" action="table.create"/>
                        <button id="editBtn" action="table.edit"/>
                        <button id="removeBtn" action="table.remove"/>
                    </buttonsPanel>
                </groupTable>
                <hbox id="lookupActions" spacing="true" visible="false">
                    <button action="lookupSelectAction"/>
                    <button action="lookupCancelAction"/>
                </hbox>
            </vbox>
            <vbox id="previewBox" height="100%" margin="false,false,false,true" expand="previewFragment"
                  spacing="true">
                <fragment id="previewFragment" screen="lupreview_ItemPreviewFragment"/>
            </vbox>
        </split>
    </layout>
</window>
```

Substitute strings between curly braces (`{}`), and the `_YOUR_ENTITY_` placeholder with your Entity name.

```java
@UiController("{SCREEN_ID}")
@UiDescriptor("{XML_FILENAME}")
@LookupComponent("table")
@LoadDataBeforeShow
public class _YOUR_ENTITY_Browse extends StandardLookup<_YOUR_ENTITY_> implements HasSelectedItemPreview {

}
```