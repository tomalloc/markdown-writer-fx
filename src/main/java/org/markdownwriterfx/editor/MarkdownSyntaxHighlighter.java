/*
 * Copyright (c) 2015 Karl Tauber <karl at jformdesigner dot com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.markdownwriterfx.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javafx.application.Platform;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.pegdown.ast.*;

/**
 * Markdown syntax highlighter.
 *
 * Uses pegdown AST.
 *
 * @author Karl Tauber
 */
class MarkdownSyntaxHighlighter
	implements Visitor
{
	private enum StyleClass {
		strong,
		em,

		// headers
		h1,
		h2,
		h3,
		h4,
		h5,
		h6,
	};

	/**
	 * style bits (1 << StyleClass.ordinal()) for each character
	 * simplifies implementation of overlapping styles
	 */
	private int[] styleClassBits;

	static void highlight(StyleClassedTextArea textArea, RootNode astRoot) {
		assert StyleClass.values().length <= 32;
		assert Platform.isFxApplicationThread();

		textArea.setStyleSpans(0, new MarkdownSyntaxHighlighter()
				.computeHighlighting(astRoot, textArea.getLength()));
	}

	private MarkdownSyntaxHighlighter() {
	}

	private StyleSpans<Collection<String>> computeHighlighting(RootNode astRoot, int textLength) {
		styleClassBits = new int[textLength];

		// visit all nodes
		astRoot.accept(this);

		// build style spans
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		if (styleClassBits.length > 0) {
			int spanStart = 0;
			int previousBits = styleClassBits[0];

			for (int i = 1; i < styleClassBits.length; i++) {
				int bits = styleClassBits[i];
				if (bits == previousBits)
					continue;

				spansBuilder.add(toStyleClasses(previousBits), i - spanStart);

				spanStart = i;
				previousBits = bits;
			}
			spansBuilder.add(toStyleClasses(previousBits), styleClassBits.length - spanStart);
		} else
			spansBuilder.add(Collections.emptyList(), 0);
		return spansBuilder.create();
	}

	private Collection<String> toStyleClasses(int bits) {
		if (bits == 0)
			return Collections.emptyList();

		Collection<String> styleClasses = new ArrayList<>(1);
		for (StyleClass styleClass : StyleClass.values()) {
			if ((bits & (1 << styleClass.ordinal())) != 0)
				styleClasses.add(styleClass.name());
		}
		return styleClasses;
	}

	@Override
	public void visit(AbbreviationNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(AnchorLinkNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(AutoLinkNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BlockQuoteNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BulletListNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(CodeNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(DefinitionListNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(DefinitionNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(DefinitionTermNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ExpImageNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ExpLinkNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(HeaderNode node) {
		StyleClass styleClass;
		switch (node.getLevel()) {
			case 1: styleClass = StyleClass.h1; break;
			case 2: styleClass = StyleClass.h2; break;
			case 3: styleClass = StyleClass.h3; break;
			case 4: styleClass = StyleClass.h4; break;
			case 5: styleClass = StyleClass.h5; break;
			case 6: styleClass = StyleClass.h6; break;
			default: return;
		}
		setStyleClass(node, styleClass);
		visitChildren(node);
	}

	@Override
	public void visit(HtmlBlockNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(InlineHtmlNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ListItemNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(MailLinkNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OrderedListNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ParaNode node) {
		// TODO Auto-generated method stub
		visitChildren(node);
	}

	@Override
	public void visit(QuotedNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ReferenceNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(RefImageNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(RefLinkNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(RootNode node) {
		// TODO Auto-generated method stub
		visitChildren(node);
	}

	@Override
	public void visit(SimpleNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(SpecialTextNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(StrikeNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(StrongEmphSuperNode node) {
		setStyleClass(node, node.isStrong() ? StyleClass.strong : StyleClass.em);
	}

	@Override
	public void visit(TableBodyNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(TableCaptionNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(TableCellNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(TableColumnNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(TableHeaderNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(TableNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(TableRowNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(VerbatimNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(WikiLinkNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(TextNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(SuperNode node) {
		// TODO Auto-generated method stub
		visitChildren(node);
	}

	@Override
	public void visit(Node node) {
		// TODO Auto-generated method stub

	}

	private void visitChildren(SuperNode node) {
		for (Node child : node.getChildren())
			child.accept(this);
	}

	private void setStyleClass(Node node, StyleClass styleClass) {
		// because PegDownProcessor.prepareSource() adds two trailing newlines
		// to the text before parsing, we need to limit the end index
		int start = node.getStartIndex();
		int end = Math.min(node.getEndIndex(), styleClassBits.length);
		int styleBit = 1 << styleClass.ordinal();

		for (int i = start; i < end; i++)
			styleClassBits[i] |= styleBit;
	}
}
