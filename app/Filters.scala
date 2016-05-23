import javax.inject._

import play.api.http.HttpFilters
import play.filters.cors.CORSFilter

/**
 * This class configures filters that run on every request.
 *
 * @param corsFilter The Cross-Origin Resource Sharing headers filter.
 */
@Singleton
class Filters @Inject() (corsFilter: CORSFilter) extends HttpFilters {
  override val filters = Seq(corsFilter)
}
