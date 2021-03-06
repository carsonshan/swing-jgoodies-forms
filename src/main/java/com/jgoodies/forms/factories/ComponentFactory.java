/*
 * Copyright (c) 2002-2015 JGoodies Software GmbH. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JGoodies Software GmbH nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jgoodies.forms.factories;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.jgoodies.common.swing.MnemonicUtils;
import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.builder.ListViewBuilder;

/**
 * An interface that defines the factory methods as used by the
 * {@link FormBuilder}, {@link ListViewBuilder}, and other builders.<p>
 *
 * The String arguments passed to the methods {@code #createLabel(String)},
 * {@code #createTitle(String)}, and
 * {@code #createSeparator(String, int)} can contain an optional
 * mnemonic marker. The mnemonic and mnemonic index are indicated
 * by a single ampersand (<tt>&amp;</tt>). For example
 * <tt>&quot;&amp;Save&quot</tt>, or <tt>&quot;Save&nbsp;&amp;as&quot</tt>.
 * To use the ampersand itself duplicate it, for example
 * <tt>&quot;Look&amp;&amp;Feel&quot</tt>.
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.10 $
 *
 * @see    DefaultComponentFactory
 * @see    FormBuilder
 */
public interface ComponentFactory {

    /**
	 * Creates and returns a button that is bound to the given Action.
	 * Useful to return customized buttons, for example, the JGoodies
	 * {@code JGButton} is bound to some custom Action properties.
	 *
	 * @param action    provides [bound] visual properties for the button
	 * @return the created button
	 *
	 * @since 1.4
	 */
	JButton createButton(Action action);


	/**
	 * Creates and returns a label with an optional mnemonic.<p>
	 *
	 * <pre>
	 * createLabel("Name");       // No mnemonic
	 * createLabel("N_ame");      // Mnemonic is 'a'
	 * createLabel("Save _as");   // Mnemonic is the second 'a'
	 * </pre>
	 *
	 * @param markedText  the label's text - may contain a mnemonic marker
	 * @return an label with optional mnemonic
	 * 
	 * @see MnemonicUtils
	 */
	JLabel createLabel(String markedText);


	/**
     * Creates and returns a label with an optional mnemonic
     * that is intended to label a read-only component.<p>
     *
     * <pre>
     * createReadOnlyLabel("Name");       // No mnemonic
     * createReadOnlyLabel("N_ame");      // Mnemonic is 'a'
     * createReadOnlyLabel("Save _as");   // Mnemonic is the second 'a'
     * </pre>
     *
     * @param markedText  the label's text - may contain a mnemonic marker
     * @return an label with optional mnemonic intended for read-only
     *     components
     *
     * @see MnemonicUtils
     *
     * @since 1.3
     */
    JLabel createReadOnlyLabel(String markedText);


    /**
     * Creates and returns a title label that uses the foreground color
     * and font of a {@code TitledBorder}.<p>
     *
     * <pre>
     * createTitle("Name");       // No mnemonic
     * createTitle("N_ame");      // Mnemonic is 'a'
     * createTitle("Save _as");   // Mnemonic is the second 'a'
     * </pre>
     *
     * @param markedText  the title's text - may contain a mnemonic marker
     * @return an emphasized title label
     * 
     * @see MnemonicUtils
     */
    JLabel createTitle(String markedText);


    /**
     * Creates and returns a label intended for pane headers that uses
     * a larger font than the control font and a special foreground color.
     * For example, on the Windows platform this method may use the font,
     * size, and color of the TaskDialog main instruction as described
     * by the MS User Experience Guide.<p>
     *
     * If the label text is marked with the mnemonic marker,
     * the mnemonic and mnemonic index will be configured.
     * For example if {@code markedText} is &quot;_Charge Codes&quot;, the text
     * will be set to &quot;Charge Codes&quot;, the mnemonic is 'C', and the
     * mnemonic index is 0.<p>
     *
     * A simple implementation may just delegate to
     * {@link #createTitle(String)}.
     *
     * @param markedText   the label text with optional mnemonic marker
     * @return a label intended for pane headers
     * 
     * @see MnemonicUtils
     *
     * @since 1.6
     */
    JLabel createHeaderLabel(String markedText);


    /**
     * Creates and returns a labeled separator. Useful to separate
     * paragraphs in a panel, which is often a better choice than a
     * {@code TitledBorder}.<p>
     *
     * <pre>
     * final int LEFT = SwingConstants.LEFT;
     * createSeparator("Name",       LEFT); // No mnemonic
     * createSeparator("N_ame",      LEFT); // Mnemonic is 'a'
     * createSeparator("Save _as",   LEFT); // Mnemonic is the second 'a'
     * </pre>
     *
     * @param markedText  the label's text - may contain a mnemonic marker
     * @param alignment text alignment, one of {@code SwingConstants.LEFT},
     *     {@code SwingConstants.CENTER}, {@code SwingConstants.RIGHT}
     * @return a title label with separator on the side
     * 
     * @see MnemonicUtils
     */
    JComponent createSeparator(String markedText, int alignment);


}
