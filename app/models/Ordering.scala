package models

/**
 * Slick 関連のパッケージのインポート
 */
import play.api.db.slick.Config.driver.simple._

/**
 * DTO の定義
 */
case class Ordering(ID: Long, createdAt: Long, customerID: Long, itemID: Long, itemCount: Long, comment: String)

/**
 * テーブルの定義
 */
class OrderingTable(tag: Tag) extends Table[Ordering](tag, "orderings") {
  def ID = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def createdAt = column[Long]("created_at", O.NotNull)
  def customerID = column[Long]("customer_id", O.NotNull)
  def itemID = column[Long]("item_id", O.NotNull )
  def itemCount = column[Long]("item_count", O.NotNull)
  def comment = column[String]("commnet", O.NotNull)
  def * = (ID, createdAt, customerID, itemID, itemCount, comment) <> (Ordering.tupled, Ordering.unapply)
  def customer = foreignKey("customer_id", customerID, OrderingDAO.customerQuery)(_.ID)
  def item = foreignKey("item_id", itemID, OrderingDAO.itemQuery)(_.ID)
}

/**
 * DAO の定義
 */
object OrderingDAO {

  /**
   * scala.slick.lifted.TableQuery オブジェクトを 3 つ作成
   */  
  lazy val customerQuery = CustomerDAO.customerQuery
  lazy val itemQuery = ItemDAO.itemQuery
  lazy val orderingQuery = TableQuery[OrderingTable]

  /**
   * 3 つテーブルを結合した上でキーワード検索
   * @param word
   */
  def search(word: String)(implicit s: Session): List[(Long, Long, Long, String, String, String, String, String, Long, String, Long, String, Long, String)] = {
    val query = for {
      ordering <- orderingQuery
      customer <- customerQuery.filter(_.ID === ordering.customerID)
      item <- itemQuery.filter(_.ID === ordering.itemID)
    } yield (ordering.ID, ordering.createdAt, customer.ID, customer.name, customer.email, customer.tel, customer.address, customer.comment, item.ID, item.name, item.price, item.comment, ordering.itemCount, ordering.comment)
    query.filter(row => ((row._4 like "%"+word+"%") || (row._5 like "%"+word+"%") || (row._6 like "%"+word+"%") || (row._7 like "%"+word+"%") || (row._8 like "%"+word+"%") || (row._10 like "%"+word+"%") || (row._12 like "%"+word+"%") || (row._14 like "%"+word+"%"))).list
  }

  /**
   * ID検索
   * @param ID
   */
  def searchByID(ID: Long)(implicit s: Session): Ordering = {
    orderingQuery.filter(_.ID === ID).first
  }

  /**
   * 作成
   * @param ordering
   */
  def create(ordering: Ordering)(implicit s: Session) {
    orderingQuery.insert(ordering)
  }

  /**
   * 更新
   * @param ordering
   */
  def update(ordering: Ordering)(implicit s: Session) {
    orderingQuery.filter(_.ID === ordering.ID).update(ordering)
  }

  /**
   * 削除
   * @param ordering
   */
  def remove(ordering: Ordering)(implicit s: Session) {
    orderingQuery.filter(_.ID === ordering.ID).delete
  }
}