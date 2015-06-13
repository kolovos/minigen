package io.dimitris.minigen.ui;

/*
 =====================================================================

  EglDocument.java

  Created by Claude Duguay
  Copyright (c) 2002

 =====================================================================
 */

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

public class EglDocument
    extends DefaultStyledDocument {

  public EglDocument() {  
	Style defaultStyle = getStyle("default");
    StyleConstants.setLeftIndent(defaultStyle, 25);
    StyleConstants.setSpaceBelow(defaultStyle, 5);

    TabStop[] tabStops = new TabStop[100];
    for (int i=0; i<100; i++){
      tabStops[i] = new TabStop(20*i);
    }
    TabSet tabSet = new TabSet(tabStops);
    StyleConstants.setTabSet(defaultStyle, tabSet);
  }

  public void insertString(int offset, String text, AttributeSet style) throws
      BadLocationException {
    super.insertString(offset, text, style);
    highlightSyntax();
  }

  public void remove(int offset, int length) throws BadLocationException {
    super.remove(offset, length);
    highlightSyntax();
  }

  public void highlightSyntax() {
	  /*
	  try {
		  String text = getText(0, getLength());
	      setCharacterAttributes(0, getLength(),
                  getStyle("default"), true);
	      
		  int start = text.indexOf("[%", 0);
		  int end = text.indexOf("%]", 0);
		  
		  StyleContext sc = StyleContext.getDefaultStyleContext();
		  AttributeSet attributeSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, new Color(255,0,0));
		  
		  setCharacterAttributes(start, end, attributeSet, true);
	  }
	  catch (Exception ex) {
		  
	  }*/
	/*
    try {
      String text = getText(0, getLength());
      setCharacterAttributes(0, getLength(),
                             getStyle("default"), true);
      RETokenizer.Token token;
      RETokenizer tokenizer = new RETokenizer(types, text);
      int typeCount = types.getTypeCount();
      while ( (token = tokenizer.nextToken()) != null) {
        int pos = token.getPos();
        String type = token.getType();
        String word = token.getText();
        int len = word.length();
        for (int i = 0; i < typeCount; i++) {
          String name = types.getName(i);
          if (type.equals(name)) {
            if (types.getColor(i) == null) {
              String style = KeywordManager.getStyleName(word);
              if (style != null) {
                setCharacterAttributes(
                    pos, len, getStyle(style), false);
              }
            }
            else {
              setCharacterAttributes(
                  pos, len, getStyle(name), false);
            }
          }
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }*/
  }
}
