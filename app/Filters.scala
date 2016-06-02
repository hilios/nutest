import javax.inject._

import play.api.http.HttpFilters
import play.filters.cors.CORSFilter

/**
  * This class configures filters that run on every request.
  *
  * @param corsFilter The Cross-Origin Resource Sharing headers filter.
  * @see https://developer.mozilla.org/en-US/docs/Web/HTTP/Access_control_CORS
  */
@Singleton
class Filters @Inject() (corsFilter: CORSFilter) extends HttpFilters {
  /**
    * Returns a list of filters to be applied to each request.
    */
  def filters = Seq(corsFilter)
}
