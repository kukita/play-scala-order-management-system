package controllers

/**
 * ① パッケージのインポート
 */
import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick._
import models._

/**
 * ② コントローラーオブジェクトの定義
 */
object CustomerController extends Controller {

  /**
   * ③ HTTP フォームデータの定義
   */
  val customerForm = Form(
    mapping(
      "ID" -> longNumber,
      "name" ->  nonEmptyText(maxLength = 140),
      "email" ->  nonEmptyText(maxLength = 140),
      "tel" ->  nonEmptyText(maxLength = 140),
      "address" ->  nonEmptyText(maxLength = 140),
      "comment" -> text(maxLength = 140)
    )(Customer.apply)(Customer.unapply)
  )
  
  /**
   * ④ 顧客情報登録フォーム表示アクションメソッドの定義
   */
  def showCreateForm() = Action { request =>
    Ok(views.html.customerCreateForm(customerForm))
  }
  
  /**
   * ⑤ 顧客情報登録アクションメソッドの定義
   */
  def create() = DBAction { implicit rs =>
    customerForm.bindFromRequest.fold(
      errors => BadRequest(views.html.customerCreateForm(errors)),
      customer => {
        CustomerDAO.create(customer)
        Redirect(routes.CustomerController.search())
      }
    )
  }
  
  /**
   * ⑥ 顧客情報検索アクションメソッドの定義
   */
  def search(word: String) = DBAction { implicit rs =>
    Ok(views.html.customerSearch(word, CustomerDAO.search(word)))
  }
  
  /**
   * ⑦ 顧客情報更新フォーム表示アクションメソッドの定義
   */
  def showUpdateForm(ID: Long) = DBAction { implicit rs =>
    Ok(views.html.customerUpdateForm(ID, customerForm.fill(CustomerDAO.searchByID(ID))))
  }

  /**
   * ⑧ 顧客情報更新アクションメソッドの定義
   */
  def update(ID: Long) = DBAction { implicit rs =>
    customerForm.bindFromRequest.fold(
      errors => BadRequest(views.html.customerUpdateForm(ID, errors)),
      customer => {
        CustomerDAO.update(customer)
        Redirect(routes.CustomerController.search())
      }
    )
  }

  /**
   * ⑨ 顧客情報削除アクションメソッドの定義
   */
  def remove(ID: Long) = DBAction { implicit rs =>
    CustomerDAO.remove(CustomerDAO.searchByID(ID))
    Redirect(routes.CustomerController.search())
  }
}
