package net.redjamjar.playscalajs

import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._

import org.scalajs.dom

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

object SPAMain {

  @JSExportTopLevel("PlayScalaJSReactBoilerplateApp")
  protected def getInstance(): this.type = this

  sealed trait Pages
  case object Home extends Pages

  val routerConfig = RouterConfigDsl[Pages]
    .buildConfig { dsl =>
      import dsl._
      (emptyRule
        | staticRoute(root, Home) ~> render(
          <.h3("Welcome to the most amazing scalajs react app ever")
        )).notFound(redirectToPage(Home)(SetRouteVia.HistoryReplace))
    }
    .renderWith(layout)

  def layout(c: RouterCtl[Pages], r: Resolution[Pages]) = {
    <.div(<.h1("Hello World"), <.div(^.cls := "container", r.render()))
  }

  @JSExport
  def main(args: Array[String]): Unit = {
    println("Application Starting...");

    val container = dom.document.getElementById("root")
    val baseUrl = BaseUrl.fromWindowOrigin / ""
    val router = Router(baseUrl, routerConfig)
    router().renderIntoDOM(container)
  }
}
