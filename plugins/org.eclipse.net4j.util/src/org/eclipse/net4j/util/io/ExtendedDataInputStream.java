/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * @author Eike Stepper
 */
public class ExtendedDataInputStream extends DataInputStream implements ExtendedDataInput
{
  public ExtendedDataInputStream(InputStream in)
  {
    super(in);
  }

  public byte[] readByteArray() throws IOException
  {
    return ExtendedIOUtil.readByteArray(this);
  }

  public String readString() throws IOException
  {
    return ExtendedIOUtil.readString(this);
  }

  public Object readObject() throws IOException, ClassNotFoundException
  {
    ObjectInputStream wrapper = new ObjectInputStream(this);
    return wrapper.readObject();
  }

  public static ExtendedDataInputStream wrap(InputStream stream)
  {
    if (stream instanceof ExtendedDataInputStream)
    {
      return (ExtendedDataInputStream)stream;
    }

    return new ExtendedDataInputStream(stream);
  }

  public static InputStream unwrap(InputStream stream)
  {
    if (stream instanceof ExtendedDataInputStream)
    {
      return ((ExtendedDataInputStream)stream).in;
    }

    return stream;
  }
}
