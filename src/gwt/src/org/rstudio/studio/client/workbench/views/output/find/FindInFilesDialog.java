/*
 * FindInFilesDialog.java
 *
 * Copyright (C) 2009-11 by RStudio, Inc.
 *
 * This program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */
package org.rstudio.studio.client.workbench.views.output.find;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.rstudio.core.client.StringUtil;
import org.rstudio.core.client.files.FileSystemItem;
import org.rstudio.core.client.widget.DirectoryChooserTextBox;
import org.rstudio.core.client.widget.ModalDialog;
import org.rstudio.core.client.widget.OperationWithInput;

import java.util.ArrayList;

public class FindInFilesDialog extends ModalDialog<FindInFilesDialog.Result>
{
   public interface Binder extends UiBinder<Widget, FindInFilesDialog>
   {
   }

   public static class Result
   {
      public Result(String query,
                    FileSystemItem path,
                    boolean regex,
                    boolean caseSensitive,
                    String[] filePatterns)
      {
         query_ = query;
         path_ = path;
         regex_ = regex;
         caseSensitive_ = caseSensitive;
         filePatterns_ = filePatterns;
      }

      public String getQuery()
      {
         return query_;
      }

      public FileSystemItem getPath()
      {
         return path_;
      }

      public boolean isRegex()
      {
         return regex_;
      }

      public boolean isCaseSensitive()
      {
         return caseSensitive_;
      }

      public String[] getFilePatterns()
      {
         return filePatterns_;
      }

      private final String query_;
      private final FileSystemItem path_;
      private final boolean regex_;
      private final boolean caseSensitive_;
      private final String[] filePatterns_;
   }

   public FindInFilesDialog(OperationWithInput<Result> operation,
                            String defaultScopeLabel)
   {
      super("Find in Files", operation);

      dirChooser_ = new DirectoryChooserTextBox("Search in:",
                                                defaultScopeLabel,
                                                null);
      dirChooser_.setText("");
      mainWidget_ = GWT.<Binder>create(Binder.class).createAndBindUi(this);

      setOkButtonCaption("Find");

      checkboxFilePattern_.addValueChangeHandler(new ValueChangeHandler<Boolean>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent)
         {
            manageFilePattern();
         }
      });
      manageFilePattern();
   }

   private void manageFilePattern()
   {
      txtFilePattern_.setEnabled(checkboxFilePattern_.getValue());
   }

   @Override
   protected Result collectInput()
   {
      String filePatterns = checkboxFilePattern_.getValue()
                            ? txtFilePattern_.getText()
                            : "";
      filePatterns = filePatterns.trim();
      ArrayList<String> list = new ArrayList<String>();
      for (String pattern : filePatterns.split(","))
      {
         String trimmedPattern = pattern.trim();
         if (trimmedPattern.length() > 0)
            list.add(trimmedPattern);
      }

      return new Result(txtSearchPattern_.getText(),
                        getEffectivePath(),
                        checkboxRegex_.getValue(),
                        checkboxCaseSensitive_.getValue(),
                        list.toArray(new String[list.size()]));
   }

   private FileSystemItem getEffectivePath()
   {
      if (StringUtil.notNull(dirChooser_.getText()).trim().length() == 0)
         return null;
      return FileSystemItem.createDir(dirChooser_.getText());
   }

   @Override
   protected boolean validate(Result input)
   {
      if (StringUtil.isNullOrEmpty(input.getQuery()))
      {
         // TODO: Show an error message here? Or disable Find button until there
         // is something to search for?
         return false;
      }

      return true;
   }

   @Override
   protected Widget createMainWidget()
   {
      return mainWidget_;
   }

   @UiField
   TextBox txtSearchPattern_;
   @UiField
   CheckBox checkboxRegex_;
   @UiField
   CheckBox checkboxCaseSensitive_;
   @UiField(provided = true)
   DirectoryChooserTextBox dirChooser_;
   @UiField
   CheckBox checkboxFilePattern_;
   @UiField
   TextBox txtFilePattern_;
   private Widget mainWidget_;
}