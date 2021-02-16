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

package uk.gov.hmrc.vatdeferralnewpaymentscheme.model.fileimport

import play.api.Logger
import play.api.libs.json.Json
import shapeless.syntax.typeable._

case class PaymentOnAccount(vrn: String)

object PaymentOnAccount extends FileImportParser[PaymentOnAccount]  {
  implicit val format = Json.format[PaymentOnAccount]

  val logger = Logger(getClass)

  def parse(line: String): PaymentOnAccount = {
    //  TODO: Discuss Validation
    if (line.startsWith("2") && line.length == 11) {
      PaymentOnAccount(line.substring(2, 11))
    }
    else {
      logger.info("File Import: Payment on account String is invalid")
      PaymentOnAccount("error") // TODO: Return an None
    }
  }

  def filter[A](item: A): Boolean = {
    item.cast[PaymentOnAccount]
      .fold(
        throw new RuntimeException("FileImport: unable to cast item as TimeToPay")
      )(ttp => ttp.vrn != "error")
  }
}