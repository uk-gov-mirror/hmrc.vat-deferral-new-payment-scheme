/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.vatdeferralnewpaymentscheme.model.directdebit

import play.api.libs.json.Json
import uk.gov.hmrc.vatdeferralnewpaymentscheme.model.DirectDebitArrangementRequest

case class DirectDebitInstructionRequest(
  sortCode:        String,
  accountNumber:   String,
  accountName:     String,
  paperAuddisFlag: Boolean,
  ddiRefNumber:    String
)

object DirectDebitInstructionRequest {

  def fixAccountName(accountName: String): String = {
    if (accountName.take(40).matches("^[0-9a-zA-Z &@()!:,+`\\-\\'\\.\\/^]{1,40}$")) {
      accountName.take(40)
    } else "NA"
  }

  def apply(
    vrn: String,
    ddar: DirectDebitArrangementRequest,
    ddiRefNumber:    String
  ): DirectDebitInstructionRequest =
      DirectDebitInstructionRequest(
      ddar.sortCode,
      ddar.accountNumber,
      fixAccountName(ddar.accountName),
      paperAuddisFlag = false,
      ddiRefNumber
    )

  implicit val format = Json.format[DirectDebitInstructionRequest]
}