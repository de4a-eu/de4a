/*
 * Copyright (C) 2023, Partners of the EU funded DE4A project consortium
 *   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
 * Author:
 *   Austrian Federal Computing Center (BRZ)
 *   Spanish Ministry of Economic Affairs and Digital Transformation -
 *     General Secretariat for Digital Administration (MAETD - SGAD)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.de4a.connector.dto;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.w3c.dom.Document;

import com.helger.commons.annotation.Nonempty;
import com.helger.peppolid.IDocumentTypeIdentifier;

/**
 * AS4 message data representation for DE4AConnector
 *
 */
@NotThreadSafe
public final class AS4MessageDTO {
  private final String senderID;
  private final String receiverID;
  private final IDocumentTypeIdentifier docTypeID;
  private final String processID;
  private Document message;

  public AS4MessageDTO(@Nonnull @Nonempty final String sSenderID, @Nonnull @Nonempty final String sReceiverID,
      @Nonnull  final IDocumentTypeIdentifier sDocTypeID, @Nonnull @Nonempty final String sProcessID) {
    senderID = sSenderID;
    receiverID = sReceiverID;
    this.docTypeID = sDocTypeID;
    this.processID = sProcessID;
  }

  @Nonnull
  @Nonempty
  public String getSenderID() {
    return senderID;
  }

  @Nonnull
  @Nonempty
  public String getReceiverID() {
    return receiverID;
  }

  @Nonnull
  public IDocumentTypeIdentifier getDocTypeID() {
    return docTypeID;
  }

  @Nonnull
  @Nonempty
  public String getProcessID() {
    return processID;
  }

  @Nullable
  public Document getMessage() {
    return message;
  }

  @Nonnull
  public AS4MessageDTO withMessage(@Nullable final Document message) {
    this.message = message;
    return this;
  }
}
