package moe.caffeine.fridgehero.data.model.realm

import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class RealmFoodCategory : RealmObject {
  @PrimaryKey
  var _id: String = BsonObjectId().toHexString()

  @Index
  var name: String = ""
  var children: RealmList<RealmFoodCategory> = realmListOf()
  val parents: RealmResults<RealmFoodCategory> by backlinks(
    RealmFoodCategory::children,
    RealmFoodCategory::class
  )

  @Ignore
  val childrenMap: Map<String, RealmFoodCategory> get() = children.associateBy { it._id }

  @Ignore
  val parentsMap: Map<String, RealmFoodCategory> get() = parents.associateBy { it._id }

  fun findTrees(): List<Map<String, RealmFoodCategory>> =
    parents.flatMap { immediateParent ->
      val branches = mutableListOf<Map<String, RealmFoodCategory>>()
      val paths = mutableListOf(mapOf(immediateParent._id to immediateParent))

      while (paths.isNotEmpty()) {
        val path = paths.removeAt(0)
        branches.add(path)
        path.values.last().parents.forEach { paths.add(path + (it._id to it)) }
      }
      branches
    }.sortedBy { it.size }
}
