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

package uk.gov.hmrc.vatdeferralnewpaymentscheme.connectors

import javax.inject.Inject
import play.api.Logger
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, NotFoundException}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.vatdeferralnewpaymentscheme.config.AppConfig
import uk.gov.hmrc.vatdeferralnewpaymentscheme.model.financialdata.FinancialData
import uk.gov.hmrc.vatdeferralnewpaymentscheme.model.obligations.{ObligationData, Obligations}

import scala.concurrent.{ExecutionContext, Future}

class DesConnector @Inject() (
  http: HttpClient,
  servicesConfig: ServicesConfig,
  appConfig: AppConfig
)(implicit ec: ExecutionContext) {

  val logger = Logger(getClass)

  private def getConfig(key: String) = servicesConfig.getConfString(key, "")

  lazy val serviceURL: String = servicesConfig.baseUrl("des-service")
  lazy val environment: String = getConfig("des-service.environment")
  lazy val authorizationToken: String = s"Bearer ${getConfig("des-service.authorization-token")}"

  val headers = Seq("Authorization" -> authorizationToken, "Environment" -> environment)
  implicit val headerCarrier: HeaderCarrier = HeaderCarrier(extraHeaders = headers)

  def getObligations(vrn: String): Future[ObligationData] = {
    val url: String = s"$serviceURL/${appConfig.getObligationsPath.replace("$vrn", vrn)}"
    http.GET[ObligationData](url) recover {
      case _: NotFoundException =>
        logger.info("No Obligations found")
        ObligationData(List.empty[Obligations])
    }
  }

  def getFinancialData(vrn: String): Future[FinancialData] = {
    val url: String = s"$serviceURL/${appConfig.getFinancialDataPath.replace("$vrn", vrn)}"
    http.GET[FinancialData](url)
  }
}