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

package uk.gov.hmrc.vatdeferralnewpaymentscheme.model.financialdata

import play.api.libs.json.Json

case class TransactionPair(
  mainTransaction: Option[String], // max length 4
  subTransaction: Option[String] // max length 4
)

case class FinancialTransactions (
  mainTransaction: Option[String], // max length 4
  subTransaction: Option[String], // max length 4
  periodKey: Option[String], // max length 4
//  chargeReference: Option[String],
  originalAmount: Option[BigDecimal],
  outstandingAmount: Option[BigDecimal]
) {

  def transactionPair =
    TransactionPair(
      mainTransaction,
      subTransaction
    )
}

object FinancialTransactions {
  implicit val format = Json.format[FinancialTransactions]
}
