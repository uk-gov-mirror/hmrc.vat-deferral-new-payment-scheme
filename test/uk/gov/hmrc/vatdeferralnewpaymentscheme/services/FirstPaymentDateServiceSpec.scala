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

package uk.gov.hmrc.vatdeferralnewpaymentscheme.services


import java.time.{LocalDateTime, ZoneId, ZonedDateTime}

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import uk.gov.hmrc.vatdeferralnewpaymentscheme.controllers.BaseSpec
import uk.gov.hmrc.vatdeferralnewpaymentscheme.service.FirstPaymentDateServiceImpl

import scala.concurrent.Future

class FirstPaymentDateServiceSpec extends BaseSpec {

  when(paymentOnAccountRepo.exists(any())).thenReturn(Future.successful(true))

  def service(zonedDateTime: ZonedDateTime) = new FirstPaymentDateServiceImpl(
    paymentOnAccountRepo,
    appConfig
  ) {
    override def now: ZonedDateTime = zonedDateTime
  }

  val zoneId: ZoneId = ZoneId.of("Europe/London")

  def zonedDateTime(
    year: Int,
    month: Int,
    day: Int
  ): ZonedDateTime = {
    ZonedDateTime.of(
      LocalDateTime.of(
        year,
        month,
        day,
        0,
        0,
        0
      ),
      zoneId
    )
  }

  val firstPoaDate: ZonedDateTime =
    zonedDateTime(2021, 3, 24)

  "first payment date" should {
    "be firstPoaDate" in {
      service(firstPoaDate).get("foo").map { date =>
        date shouldBe firstPoaDate
      }
    }
    "not be firstPoaDate" in {
      service(zonedDateTime(2021, 4, 1)).get("foo").map { date =>
        date.isAfter(firstPoaDate) shouldBe true
      }
    }
  }

}
