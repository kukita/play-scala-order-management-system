package models

import play.api.db.slick.Config.driver.simple._

/**
 * DTO の定義
 */
case class Item(ID: Long, name: String, price: Long, comment: String)

/**
 * テーブルの定義
 */
class ItemTable(tag: Tag) extends Table[Item](tag, "items") {
  def ID = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def price = column[Long]("price", O.NotNull)
  def comment = column[String]("comment", O.NotNull)
  def * = (ID, name, price, comment) <> (Item.tupled, Item.unapply)
}

/**
 * DAO の定義
 */
object ItemDAO {
  lazy val itemQuery = TableQuery[ItemTable]
  
  /**
   * キーワード検索
   * @param word
   */
  def search(word: String)(implicit s: Session): List[Item] = {
    itemQuery.filter(row => (row.name like "%"+word+"%") || (row.comment like "%"+word+"%")).list
  }
  
  /**
   * ID検索
   * @param ID
   */
  def searchByID(ID: Long)(implicit s: Session): Item = {
    itemQuery.filter(_.ID === ID).first
  }
  
  /**
   * 作成
   * @param item
   */
  def create(item: Item)(implicit s: Session) {
    itemQuery.insert(item)
  }
  
  /**
   * 更新
   * @param item
   */
  def update(item: Item)(implicit s: Session) {
    itemQuery.filter(_.ID === item.ID).update(item)
  }
  
  /**
   * 削除
   * @param item
   */
  def remove(item: Item)(implicit s: Session) {
    itemQuery.filter(_.ID === item.ID).delete
  }
}