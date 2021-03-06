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

package com.jgoodies.forms.builder;

import static com.jgoodies.common.base.Preconditions.checkArgument;

import java.awt.Component;
import java.awt.FocusTraversalPolicy;
import java.lang.ref.WeakReference;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.jgoodies.common.swing.MnemonicUtils;
import com.jgoodies.forms.FormsSetup;
import com.jgoodies.forms.factories.ComponentFactory;
import com.jgoodies.forms.internal.AbstractFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * An general purpose panel builder that uses the {@link FormLayout}
 * to lay out {@code JPanel}s. It provides convenience methods
 * to set a default border and to add labels, titles and titled separators.<p>
 *
 * The PanelBuilder is the working horse for layouts when more specialized
 * builders like the {@link ButtonBarBuilder} or {@link DefaultFormBuilder}
 * are inappropriate.<p>
 *
 * The Forms tutorial includes several examples that present and compare
 * different style to build with the PanelBuilder: static row numbers
 * vs. row variable, explicit CellConstraints vs. builder cursor,
 * static rows vs. dynamically added rows. Also, you may check out the
 * Tips &amp; Tricks section of the Forms HTML documentation.<p>
 *
 * The text arguments passed to the methods {@code #addLabel},
 * {@code #addTitle}, and {@code #addSeparator} can contain
 * an optional mnemonic marker. The mnemonic and mnemonic index
 * are indicated by a single ampersand (<tt>&amp;</tt>). For example
 * <tt>&quot;&amp;Save&quot</tt>, or <tt>&quot;Save&nbsp;&amp;as&quot</tt>.
 * To use the ampersand itself duplicate it, for example
 * <tt>&quot;Look&amp;&amp;Feel&quot</tt>.<p>
 *
 * <strong>Example:</strong><br>
 * This example creates a panel with 3 columns and 3 rows.
 * <pre>
 * FormLayout layout = new FormLayout(
 *      "pref, $lcgap, 50dlu, $rgap, default",  // columns
 *      "pref, $lg, pref, $lg, pref");          // rows
 *
 * PanelBuilder builder = new PanelBuilder(layout);
 * builder.addLabel("_Title:",        CC.xy  (1, 1));
 * builder.add(new JTextField(),      CC.xywh(3, 1, 3, 1));
 * builder.addLabel("_Price:",        CC.xy  (1, 3));
 * builder.add(new JTextField(),      CC.xy  (3, 3));
 * builder.addLabel("_Author:",       CC.xy  (1, 5));
 * builder.add(new JTextField(),      CC.xy  (3, 5));
 * builder.add(new JButton("\u2026"), CC.xy  (5, 5));
 * return builder.getPanel();
 * </pre>
 *
 * @author  Karsten Lentzsch
 * @version $Revision: 1.21 $
 *
 * @see	com.jgoodies.forms.factories.ComponentFactory
 * @see     I15dPanelBuilder
 * @see     DefaultFormBuilder
 * 
 * @deprecated Replaced by {@link FormBuilder}. However, this class
 *     will remain in the Forms library for the next versions.
 */
@Deprecated
public class PanelBuilder extends AbstractFormBuilder<PanelBuilder> {

    
    // Constants **************************************************************

    /**
     * A JComponent client property that is used to determine the label
     * labeling a component. Copied from the JLabel class.
     */
    private static final String LABELED_BY_PROPERTY = "labeledBy";


    // Instance Fields ********************************************************

    /**
     * The instance value for the setLabelFor feature.
     * Is initialized using the global default.
     *
     * @see #setLabelForFeatureEnabledDefault(boolean)
     * @see #setLabelForFeatureEnabledDefault(boolean)
     */
    private boolean labelForFeatureEnabled;


    /**
     * Refers to the most recently added label.
     * Used to invoke {@link JLabel#setLabelFor(java.awt.Component)}
     * for the next component added to the panel that is applicable for
     * this feature (for example focusable). After the association
     * has been set, the reference will be cleared.
     *
     * @see #add(Component, CellConstraints)
     */
    private WeakReference<JLabel> mostRecentlyAddedLabelReference = null;


    // Instance Creation ******************************************************

    /**
     * Constructs a {@code PanelBuilder} for the given
     * layout. Uses an instance of {@code JPanel} as layout container
     * with the given layout as layout manager.
     *
     * @param layout  the FormLayout to use
     *
     * @throws NullPointerException if {@code layout} is {@code null}
     */
    public PanelBuilder(FormLayout layout){
        this(layout, new JPanel(null));
    }

    /**
     * Constructs a {@code PanelBuilder} for the given
     * FormLayout and layout container.
     *
     * @param layout  the FormLayout to use
     * @param panel   the layout container to build on
     *
     * @throws NullPointerException if {@code layout} or {@code container} is {@code null}
     */
    public PanelBuilder(FormLayout layout, JPanel panel){
        super(layout, panel);
        opaque(FormsSetup.getOpaqueDefault());
        labelForFeatureEnabled = FormsSetup.getLabelForFeatureEnabledDefault();
    }


    // Modern (Cascading) Style Configuration *********************************

    /**
     * Sets the panel's focus traversal policy and sets the panel
     * as focus traversal policy provider. Hence, this call is equivalent to:
     * <pre>
     * builder.getPanel().setFocusTraversalPolicy(policy);
     * builder.getPanel().setFocusTraversalPolicyProvider(true);
     * </pre>
     *
     * @param policy   the focus traversal policy that will manage
     * 	keyboard traversal of the children in this builder's panel
     *
     * @see JComponent#setFocusTraversalPolicy(FocusTraversalPolicy)
     * @see JComponent#setFocusTraversalPolicyProvider(boolean)
     *
     * @since 1.7
     */
    public PanelBuilder focusTraversal(FocusTraversalPolicy policy) {
        getPanel().setFocusTraversalPolicy(policy);
        getPanel().setFocusTraversalPolicyProvider(true);
        return this;
    }


    /**
     * Enables or disables the setLabelFor feature for this PanelBuilder.
     * The value is initialized from the global default value
     * {@link FormsSetup#getLabelForFeatureEnabledDefault()}.
     * It is globally disabled by default.
     *
     * @param b true for enabled, false for disabled
     */
    public PanelBuilder labelForFeatureEnabled(boolean b) {
        labelForFeatureEnabled = b;
        return this;
    }


    // Building ***************************************************************

    /**
     * Returns the panel used to build the form.
     * Intended to return the panel in build methods.
     *
     * @return the panel used by this builder to build the form
     *
     * @since 1.6
     */
    @Override
    public final JPanel build() {
        return getPanel();
    }


    // Adding Labels **********************************************************

    /**
     * Adds a textual label to the form using the default constraints.<p>
     *
     * <pre>
     * addLabel("Name:");       // No Mnemonic
     * addLabel("N_ame:");      // Mnemonic is 'a'
     * addLabel("Save _as:");   // Mnemonic is the second 'a'
     * </pre>
     *
     * @param markedText   the label's text - may contain a mnemonic marker
     * @return the new label
     *
     * @see MnemonicUtils
     * @see ComponentFactory
     */
    public final JLabel addLabel(String markedText) {
        return addLabel(markedText, cellConstraints());
    }


    /**
     * Adds a textual label to the form using the specified constraints.<p>
     *
     * <pre>
     * addLabel("Name:",       CC.xy(1, 1)); // No Mnemonic
     * addLabel("N_ame:",      CC.xy(1, 1)); // Mnemonic is 'a'
     * addLabel("Save _as:",   CC.xy(1, 1)); // Mnemonic is the second 'a'
     * </pre>
     *
     * @param markedText   the label's text - may contain a mnemonic marker
     * @param constraints  the label's cell constraints
     * @return the new label
     *
     * @see MnemonicUtils
     * @see ComponentFactory
     */
    public final JLabel addLabel(String markedText, CellConstraints constraints) {
        JLabel label = getComponentFactory().createLabel(markedText);
        add(label, constraints);
        return label;
    }


    /**
     * Adds a textual label to the form using the specified constraints.<p>
     *
     * <pre>
     * addLabel("Name:",       "1, 1"); // No Mnemonic
     * addLabel("N_ame:",      "1, 1"); // Mnemonic is 'a'
     * addLabel("Save _as:",   "1, 1"); // Mnemonic is the second 'a'
     * </pre>
     *
     * @param markedText    the label's text - may contain a mnemonic marker
     * @param encodedConstraints  a string representation for the constraints
     * @return the new label
     *
     * @see MnemonicUtils
     * @see ComponentFactory
     */
    public final JLabel addLabel(String markedText, String encodedConstraints) {
        return addLabel(markedText, new CellConstraints(encodedConstraints));
    }


    /**
     * Adds a label and component to the panel using the given cell constraints.
     * Sets the given label as <i>the</i> component label using
     * {@link JLabel#setLabelFor(java.awt.Component)}.<p>
     *
     * <strong>Note:</strong> The {@link CellConstraints} objects for the label
     * and the component must be different. Cell constraints are implicitly
     * cloned by the {@code FormLayout} when added to the container.
     * However, in this case you may be tempted to reuse a
     * {@code CellConstraints} object in the same way as with many other
     * builder methods that require a single {@code CellConstraints}
     * parameter.
     * The pitfall is that the methods {@code CellConstraints.xy*(...)}
     * just set the coordinates but do <em>not</em> create a new instance.
     * And so the second invocation of {@code xy*(...)} overrides
     * the settings performed in the first invocation before the object
     * is cloned by the {@code FormLayout}.<p>
     *
     * <strong>Wrong:</strong><pre>
     * builder.addLabel(
     *     "_Name:",            // Mnemonic is 'N'
     *     cc.xy(1, 7),         // will be modified by the code below
     *     nameField,
     *     cc.xy(3, 7)          // sets the single instance to (3, 7)
     * );
     * </pre>
     * <strong>Correct:</strong><pre>
     * builder.addLabel(
     *     "_Name:",
     *     CC.xy(1, 7),         // creates an instance
     *     nameField,
     *     CC.xy(3, 7)          // creates another instance
     * );
     * </pre>
     *
     * @param markedText            the label's text - may contain a mnemonic marker
     * @param labelConstraints      the label's cell constraints
     * @param component             the component to add
     * @param componentConstraints  the component's cell constraints
     * @return the added label
     * @throws IllegalArgumentException if the same cell constraints instance
     *     is used for the label and the component
     *
     * @see JLabel#setLabelFor(java.awt.Component)
     * @see MnemonicUtils
     * @see ComponentFactory
     * @see DefaultFormBuilder
     */
    public final JLabel addLabel(
        String markedText, CellConstraints labelConstraints,
        Component component,     CellConstraints componentConstraints) {

        if (labelConstraints == componentConstraints) {
            throw new IllegalArgumentException(
                    "You must provide two CellConstraints instances, " +
                    "one for the label and one for the component.\n" +
                    "Consider using the CC class. See the JavaDocs for details.");
        }

        JLabel label = addLabel(markedText, labelConstraints);
        add(component, componentConstraints);
        label.setLabelFor(component);
        return label;
    }


    // Adding Labels for Read-Only Components ---------------------------------

    /**
     * Adds a textual label intended for labeling read-only components
     * to the form using the default constraints.<p>
     *
     * <pre>
     * addROLabel("Name:");       // No Mnemonic
     * addROLabel("N_ame:");      // Mnemonic is 'a'
     * addROLabel("Save _as:");   // Mnemonic is the second 'a'
     * </pre>
     *
     * @param markedText   the label's text - may contain a mnemonic marker
     * @return the new label
     *
     * @see MnemonicUtils
     * @since 1.3
     */
    public final JLabel addROLabel(String markedText) {
        return addROLabel(markedText, cellConstraints());
    }


    /**
     * Adds a textual label intended for labeling read-only components
     * to the form using the specified constraints.<p>
     *
     * <pre>
     * addROLabel("Name:",       CC.xy(1, 1)); // No Mnemonic
     * addROLabel("N_ame:",      CC.xy(1, 1)); // Mnemonic is 'a'
     * addROLabel("Save _as:",   CC.xy(1, 1)); // Mnemonic is the second 'a'
     * </pre>
     *
     * @param markedText        the label's text - may contain a mnemonic marker
     * @param constraints       the label's cell constraints
     * @return the new label
     *
     * @see MnemonicUtils
     * 
     * @since 1.3
     */
    public final JLabel addROLabel(String markedText, CellConstraints constraints) {
        JLabel label = getComponentFactory().createReadOnlyLabel(markedText);
        add(label, constraints);
        return label;
    }


    /**
     * Adds a textual label intended for labeling read-only components
     * to the form using the specified constraints.<p>
     *
     * <pre>
     * addROLabel("Name:",       "1, 1"); // No Mnemonic
     * addROLabel("N_ame:",      "1, 1"); // Mnemonic is 'a'
     * addROLabel("Save _as:",   "1, 1"); // Mnemonic is the second 'a'
     * </pre>
     *
     * @param markedText    the label's text - may contain a mnemonic marker
     * @param encodedConstraints  a string representation for the constraints
     * @return the new label
     *
     * @see MnemonicUtils
     * 
     * @since 1.3
     */
    public final JLabel addROLabel(String markedText, String encodedConstraints) {
        return addROLabel(markedText, new CellConstraints(encodedConstraints));
    }


    /**
     * Adds a label and component to the panel using the given cell constraints.
     * Sets the given label as <i>the</i> component label using
     * {@link JLabel#setLabelFor(java.awt.Component)}.<p>
     *
     * <strong>Note:</strong> The {@link CellConstraints} objects for the label
     * and the component must be different. Cell constraints are implicitly
     * cloned by the FormLayout when added to the container.
     * However, in this case you may be tempted to reuse a
     * {@code CellConstraints} object in the same way as with many other
     * builder methods that require a single {@code CellConstraints}
     * parameter.
     * The pitfall is that the methods {@code CellConstraints.xy*(...)}
     * just set the coordinates but do <em>not</em> create a new instance.
     * And so the second invocation of {@code xy*(...)} overrides
     * the settings performed in the first invocation before the object
     * is cloned by the {@code FormLayout}.<p>
     *
     * <strong>Wrong:</strong><pre>
     * builder.addROLabel(
     *     "_Name:",            // Mnemonic is 'N'
     *     cc.xy(1, 7),         // will be modified by the code below
     *     nameField,
     *     cc.xy(3, 7)          // sets the single instance to (3, 7)
     * );
     * </pre>
     * <strong>Correct:</strong><pre>
     * builder.addROLabel(
     *     "_Name:",
     *     CC.xy(1, 7),          // creates an instance
     *     nameField,
     *     CC.xy(3, 7)           // creates another instance
     * );
     * </pre>
     *
     * @param markedText            the label's text - may contain a mnemonic marker
     * @param labelConstraints      the label's cell constraints
     * @param component             the component to add
     * @param componentConstraints  the component's cell constraints
     * @return the added label
     * @throws IllegalArgumentException if the same cell constraints instance
     *     is used for the label and the component
     *
     * @see JLabel#setLabelFor(java.awt.Component)
     * @see MnemonicUtils
     * @see DefaultFormBuilder
     *
     * @since 1.3
     */
    public final JLabel addROLabel(
        String markedText, CellConstraints labelConstraints,
        Component component,     CellConstraints componentConstraints) {
        checkConstraints(labelConstraints, componentConstraints);
        JLabel label = addROLabel(markedText, labelConstraints);
        add(component, componentConstraints);
        label.setLabelFor(component);
        return label;
    }


    // Adding Titles ----------------------------------------------------------

    /**
     * Adds a title label to the form using the default constraints.<p>
     *
     * <pre>
     * addTitle("Name");       // No mnemonic
     * addTitle("N_ame");      // Mnemonic is 'a'
     * addTitle("Save :as");   // Mnemonic is the second 'a'
     * </pre>
     *
     * @param markedText   the title label's text - may contain a mnemonic marker
     * @return the added title label
     *
     * @see MnemonicUtils
     * @see ComponentFactory
     */
    public final JLabel addTitle(String markedText) {
        return addTitle(markedText, cellConstraints());
    }


    /**
     * Adds a title label to the form using the specified constraints.<p>
     *
     * <pre>
     * addTitle("Name",       CC.xy(1, 1)); // No mnemonic
     * addTitle("N_ame",      CC.xy(1, 1)); // Mnemonic is 'a'
     * addTitle("Save _as",   CC.xy(1, 1)); // Mnemonic is the second 'a'
     * </pre>
     *
     * @param markedText   the title label's text - may contain a mnemonic marker
     * @param constraints        the separator's cell constraints
     * @return the added title label
     *
     * @see MnemonicUtils
     * @see ComponentFactory
     */
    public final JLabel addTitle(String markedText, CellConstraints constraints) {
        JLabel titleLabel = getComponentFactory().createTitle(markedText);
        add(titleLabel, constraints);
        return titleLabel;
    }


    /**
     * Adds a title label to the form using the specified constraints.<p>
     *
     * <pre>
     * addTitle("Name",       "1, 1"); // No mnemonic
     * addTitle("N_ame",      "1, 1"); // Mnemonic is 'a'
     * addTitle("Save _as",   "1, 1"); // Mnemonic is the second 'a'
     * </pre>
     *
     * @param markedText   the title label's text - may contain a mnemonic marker
     * @param encodedConstraints  a string representation for the constraints
     * @return the added title label
     *
     * @see MnemonicUtils
     * @see ComponentFactory
     */
    public final JLabel addTitle(String markedText, String encodedConstraints) {
        return addTitle(markedText, new CellConstraints(encodedConstraints));
    }


    // Adding Separators ------------------------------------------------------

    /**
     * Adds a titled separator to the form that spans all columns.<p>
     *
     * <pre>
     * addSeparator("Name");       // No Mnemonic
     * addSeparator("N_ame");      // Mnemonic is 'a'
     * addSeparator("Save _as");   // Mnemonic is the second 'a'
     * </pre>
     *
     * @param markedText   the separator label's text - may contain a mnemonic marker
     * @return the added separator
     * 
     * @see MnemonicUtils
     */
    public final JComponent addSeparator(String markedText) {
        return addSeparator(markedText, getLayout().getColumnCount());
    }


    /**
     * Adds a titled separator to the form using the specified constraints.<p>
     *
     * <pre>
     * addSeparator("Name",       CC.xy(1, 1)); // No Mnemonic
     * addSeparator("N_ame",      CC.xy(1, 1)); // Mnemonic is 'a'
     * </pre>
     *
     * @param markedText   the separator label's text - may contain a mnemonic marker
     * @param constraints  the separator's cell constraints
     * @return the added separator
     * 
     * @see MnemonicUtils
     */
    public final JComponent addSeparator(String markedText, CellConstraints constraints) {
        int titleAlignment = isLeftToRight()
                ? SwingConstants.LEFT
                : SwingConstants.RIGHT;
        JComponent titledSeparator =
            getComponentFactory().createSeparator(markedText, titleAlignment);
        add(titledSeparator, constraints);
        return titledSeparator;
    }


    /**
     * Adds a titled separator to the form using the specified constraints.<p>
     *
     * <pre>
     * addSeparator("Name",       "1, 1"); // No Mnemonic
     * addSeparator("N_ame",      "1, 1"); // Mnemonic is 'a'
     * </pre>
     *
     * @param markedText   the separator label's text - may contain a mnemonic marker
     * @param encodedConstraints  a string representation for the constraints
     * @return the added separator
     * 
     * @see MnemonicUtils
     */
    public final JComponent addSeparator(String markedText, String encodedConstraints) {
        return addSeparator(markedText, new CellConstraints(encodedConstraints));
    }


    /**
     * Adds a titled separator to the form that spans the specified columns.<p>
     *
     * <pre>
     * addSeparator("Name",       3); // No Mnemonic
     * addSeparator("N_ame",      3); // Mnemonic is 'a'
     * </pre>
     *
     * @param markedText   the separator label's text - may contain a mnemonic marker
     * @param columnSpan	the number of columns the separator spans
     * @return the added separator
     * 
     * @see MnemonicUtils
     */
    public final JComponent addSeparator(String markedText, int columnSpan) {
        return addSeparator(markedText, createLeftAdjustedConstraints(columnSpan));
    }


    /**
     * Adds a label and component to the panel using the given cell constraints.
     * Sets the given label as <i>the</i> component label using
     * {@link JLabel#setLabelFor(java.awt.Component)}.<p>
     *
     * <strong>Note:</strong> The {@link CellConstraints} objects for the label
     * and the component must be different. Cell constraints are implicitly
     * cloned by the {@code FormLayout} when added to the container.
     * However, in this case you may be tempted to reuse a
     * {@code CellConstraints} object in the same way as with many other
     * builder methods that require a single {@code CellConstraints}
     * parameter.
     * The pitfall is that the methods {@code CellConstraints.xy*(...)}
     * just set the coordinates but do <em>not</em> create a new instance.
     * And so the second invocation of {@code xy*(...)} overrides
     * the settings performed in the first invocation before the object
     * is cloned by the {@code FormLayout}.<p>
     *
     * <strong>Wrong:</strong><pre>
     * CellConstraints cc = new CellConstraints();
     * builder.add(
     *     nameLabel,
     *     cc.xy(1, 7),         // will be modified by the code below
     *     nameField,
     *     cc.xy(3, 7)          // sets the single instance to (3, 7)
     * );
     * </pre>
     * <strong>Correct:</strong><pre>
     * builder.add(
     *     nameLabel,
     *     CC.xy(1, 7),         // creates an instance
     *     nameField,
     *     CC.xy(3, 7)          // creates another instance
     * );
     * </pre>
     *
     * @param label                 the label to add
     * @param labelConstraints      the label's cell constraints
     * @param component             the component to add
     * @param componentConstraints  the component's cell constraints
     * @return the added label
     * @throws IllegalArgumentException if the same cell constraints instance
     *     is used for the label and the component
     *
     * @see JLabel#setLabelFor(java.awt.Component)
     * @see DefaultFormBuilder
     */
    public final JLabel add(JLabel label,        CellConstraints labelConstraints,
                            Component component, CellConstraints componentConstraints) {
        checkConstraints(labelConstraints, componentConstraints);
        add(label,     labelConstraints);
        add(component, componentConstraints);
        label.setLabelFor(component);
        return label;
    }


    // Overriding Superclass Behavior *****************************************

    /**
     * Adds a component to the panel using the given cell constraints.
     * In addition to the superclass behavior, this implementation
     * tracks the most recently added label, and associates it with
     * the next added component that is applicable for being set as component
     * for the label.
     *
     * @param component        the component to add
     * @param cellConstraints  the component's cell constraints
     * @return the added component
     *
     * @see #isLabelForApplicable(JLabel, Component)
     */
    @Override
    public Component add(Component component, CellConstraints cellConstraints) {
        Component result = super.add(component, cellConstraints);
        manageLabelsAndComponents(component);
        return result;
    }


    // Default Behavior *******************************************************

    private void manageLabelsAndComponents(Component c) {
        if (!labelForFeatureEnabled) {
            return;
        }
        if (c instanceof JLabel) {
            JLabel label = (JLabel) c;
            if (label.getLabelFor() == null) {
                setMostRecentlyAddedLabel(label);
            } else {
                clearMostRecentlyAddedLabel();
            }
            return;
        }
        JLabel mostRecentlyAddedLabel = getMostRecentlyAddedLabel();
        if (   mostRecentlyAddedLabel != null
            && isLabelForApplicable(mostRecentlyAddedLabel, c)) {
            setLabelFor(mostRecentlyAddedLabel, c);
            clearMostRecentlyAddedLabel();
        }
    }
    
    
    /**
     * Checks and answers whether the given component shall be set
     * as component for a previously added label using
     * {@link JLabel#setLabelFor(Component)}.
     *
     * This default implementation checks whether the component is focusable,
     * and - if a JComponent - whether it is already labeled by a JLabel.
     * Subclasses may override.
     *
     * @param label        the candidate for labeling {@code component}
     * @param component    the component that could be labeled by {@code label}
     * @return true if focusable, false otherwise
     */
    protected boolean isLabelForApplicable(JLabel label, Component component) {
        // 1) Is the label labeling a component?
        if (label.getLabelFor() != null) {
            return false;
        }

        // 2) Is the component focusable?
        if (!component.isFocusable()) {
            return false;
        }

        // 3) Is the component labeled by another label?
        if (!(component instanceof JComponent)) {
            return true;
        }
        JComponent c = (JComponent) component;
        return c.getClientProperty(LABELED_BY_PROPERTY) == null;
    }


    /**
     * Sets {@code label} as labeling label for {@code component} or an
     * appropriate child. In case of a JScrollPane as given component,
     * this default implementation labels the view of the scroll pane's
     * viewport.
     *
     * @param label      the labeling label
     * @param component  the component to be labeled, or the parent of
     *   the labeled component
     */
    protected void setLabelFor(JLabel label, Component component) {
        Component labeledComponent;
        if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) component;
            labeledComponent = scrollPane.getViewport().getView();
        } else {
            labeledComponent = component;
        }
        label.setLabelFor(labeledComponent);
    }


    // Helper Code ************************************************************

    /**
     * Returns the most recently added JLabel that has a mnemonic set
     * - if any, {@code null}, if none has been set, or if it has
     * been cleared after setting an association before, or if it has been
     * cleared by the garbage collector.
     *
     * @return the most recently added JLabel that has a mnemonic set
     *     and has not been associated with a component applicable for this
     *     feature. {@code null} otherwise.
     */
    private JLabel getMostRecentlyAddedLabel() {
        if (mostRecentlyAddedLabelReference == null) {
            return null;
        }
        JLabel label = mostRecentlyAddedLabelReference.get();
        if (label == null) {
            return null;
        }
        return label;
    }


    /**
     * Sets the given label as most recently added label using a weak reference.
     *
     * @param label  the label to be set
     */
    private void setMostRecentlyAddedLabel(JLabel label) {
        mostRecentlyAddedLabelReference = new WeakReference<>(label);
    }


    /**
     * Clears the reference to the most recently added mnemonic label.
     */
    private void clearMostRecentlyAddedLabel() {
        mostRecentlyAddedLabelReference = null;
    }


    private static void checkConstraints(CellConstraints c1, CellConstraints c2) {
        checkArgument(c1 != c2,
                "You must provide two CellConstraints instances, " +
                "one for the label and one for the component.\n" +
                "Consider using the CC factory. See the JavaDocs for details.");
    }


}
