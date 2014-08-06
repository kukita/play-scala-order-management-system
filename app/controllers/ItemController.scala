package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick._
import models._

object ItemController extends Controller {

  val itemForm = Form(
    mapping(
      "ID" -> longNumber,
      "name" ->  nonEmptyText(maxLength = 140),
      "price" ->  longNumber,
      "comment" -> text(maxLength = 140)
    )(Item.apply)(Item.unapply)
  )
  
  def showCreateForm() = Action { request =>
    Ok(views.html.itemCreateForm(itemForm))
  }
  
  def create() = DBAction { implicit rs =>
    itemForm.bindFromRequest.fold(
      errors => BadRequest(views.html.itemCreateForm(errors)),
      item => {
        ItemDAO.create(item)
        Redirect(routes.ItemController.search())
      }
    )
  }
  
  def search(word: String) = DBAction { implicit rs =>
    Ok(views.html.itemSearch(word, ItemDAO.search(word)))
  }
  
  def showUpdateForm(ID: Long) = DBAction { implicit rs =>
    Ok(views.html.itemUpdateForm(ID, itemForm.fill(ItemDAO.searchByID(ID))))
  }
  
  def update(ID: Long) = DBAction { implicit rs =>
    itemForm.bindFromRequest.fold(
      errors => BadRequest(views.html.itemUpdateForm(ID, errors)),
      item => {
        ItemDAO.update(item)
        Redirect(routes.ItemController.search())
      }
    )
  }
  
  def remove(ID: Long) = DBAction { implicit rs =>
    ItemDAO.remove(ItemDAO.searchByID(ID))
    Redirect(routes.ItemController.search())
  }
}
