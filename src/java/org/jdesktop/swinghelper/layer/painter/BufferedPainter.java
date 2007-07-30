/*
 * Copyright (C) 2006,2007 Alexander Potochkin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jdesktop.swinghelper.layer.painter;

import org.jdesktop.swinghelper.layer.JXLayer;
import org.jdesktop.swinghelper.layer.effect.*;

import javax.swing.*;
import java.awt.*;

/**
 * The implementation of the {@link AbstractBufferedPainter}
 * which takes a delegate painter and renders it to the back buffer,
 * which allows to apply any {@link Effect}s after that
 * <p/>
 * If no painter is provided, the {@link DefaultPainter} will be used
 * as the delegate painter
 * <p/>
 * Note: When a delegate painter changes its state
 * and calls its {@link AbstractPainter#fireLayerItemChanged()}
 * the {@link BufferedPainter} will fire change event as well
 * and its {@link JXLayer}s will eventually be repainted
 *
 * @see Effect
 * @see ImageOpEffect
 * @see #setEffects(Effect...)
 */
public class BufferedPainter<V extends JComponent>
        extends AbstractBufferedPainter<V> {

    private Painter<V> painter;

    /**
     * Creates a new {@link BufferedPainter}
     * with a new {@link DefaultPainter} as delegate painter
     *
     * @see #getDelegatePainter()
     * @see #setDelegatePainter(Painter)
     */
    public BufferedPainter() {
        this(new DefaultPainter<V>(), (Effect[]) null);
    }

    /**
     * Creates a new {@link BufferedPainter}
     * with the given <code>painter</code> as the delegate painter
     *
     * @param painter the painter to be use as the delegate painter
     */
    public BufferedPainter(Painter<V> painter) {
        this(painter, (Effect[]) null);
    }

    /**
     * Creates a new {@link BufferedPainter}
     * with a given collection of the {@link Effect}s
     * and new {@link DefaultPainter} as the delegate painter
     *
     * @param effects the collection of the {@link Effect}s
     *                to be applied to the buffer of this painter
     * @see Effect
     * @see ImageOpEffect
     */
    public BufferedPainter(Effect... effects) {
        this(new DefaultPainter<V>(), effects);
    }

    /**
     * Creates a new {@link BufferedPainter}
     * with the given <code>painter</code> as the delegate painter
     * and the given collection of the {@link Effect}s
     *
     * @param painter the painter to be use as the delegate painter
     * @param effects the collection of the {@link Effect}s
     *                to be applied to the buffer of this painter
     */
    public BufferedPainter(Painter<V> painter, Effect... effects) {
        setDelegatePainter(painter);
        setEffects(effects);
        getModel().setIncrementalUpdate(true);
    }

    /**
     * Gets the delegate painter for this {@link BufferedPainter}
     *
     * @return the delegate painter for this {@link BufferedPainter}
     */
    public Painter<V> getDelegatePainter() {
        return painter;
    }

    /**
     * Sets the delegate painter for this {@link BufferedPainter}
     * <p/>
     * Note: When a delegate painter changes its state
     * and calls its {@link AbstractPainter#fireLayerItemChanged()}
     * the {@link BufferedPainter} will fire change event as well
     * and its {@link JXLayer}s will eventually be repainted
     *
     * @param painter the delegate painter for this {@link BufferedPainter}
     */
    public void setDelegatePainter(Painter<V> painter) {
        if (this.painter != painter) {
            if (this.painter != null) {
                this.painter.removeLayerItemListener(this);
            }
            if (painter != null) {
                painter.addLayerItemListener(this);
            }
            this.painter = painter;
            validate();
            fireLayerItemChanged();
        }
    }

    /**
     * {@inheritDoc} 
     */
    protected boolean isPainterValid() {
        return getDelegatePainter() != null;
    }

    /**
     * Delegates painting to the delegate painter
     *  
     * @param g2 the {@link java.awt.Graphics2D} to render to
     * @param l the {@link JXLayer} to render for
     * 
     * @see #getDelegatePainter() 
     * @see #setDelegatePainter(Painter) 
     */
    protected void paintToBuffer(Graphics2D g2, JXLayer<V> l) {
        getDelegatePainter().paint(g2, l);
    }
}
