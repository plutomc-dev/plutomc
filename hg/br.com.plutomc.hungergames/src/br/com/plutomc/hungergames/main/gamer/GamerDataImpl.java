package br.com.plutomc.hungergames.main.gamer;

import br.com.plutomc.core.common.CommonConst;
import br.com.plutomc.core.common.backend.Query;
import br.com.plutomc.core.common.backend.mongodb.MongoConnection;
import br.com.plutomc.core.common.backend.mongodb.MongoQuery;
import br.com.plutomc.core.common.utils.json.JsonBuilder;
import br.com.plutomc.core.common.utils.json.JsonUtils;
import br.com.plutomc.hungergames.engine.backend.GamerData;
import br.com.plutomc.hungergames.engine.gamer.Gamer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.UUID;

public class GamerDataImpl implements GamerData<GamerImpl> {

	private Query<JsonElement> query;

	public GamerDataImpl(MongoConnection mongoConnection) {
		this.query = createDefault(mongoConnection);
	}

	@Override
	public void createGamer(Gamer gamer) {
		boolean needCreate = query.findOne("uniqueId", gamer.getUniqueId().toString()) == null;

		if (needCreate)
			query.create(new String[] { CommonConst.GSON.toJson(gamer) });
	}

	@Override
	public GamerImpl loadGamer(UUID uniqueId) {
		JsonElement found = query.findOne("uniqueId", uniqueId.toString());
		return found == null ? null : CommonConst.GSON.fromJson(CommonConst.GSON.toJson(found), GamerImpl.class);
	}

	@Override
	public void deleteGamer(UUID uniqueId) {
		boolean needCreate = query.findOne("uniqueId", uniqueId.toString()) == null;

		if (!needCreate)
			query.deleteOne("uniqueId", uniqueId.toString());
	}

	@Override
	public void updateGamer(Gamer gamer, String fieldName) {
		JsonObject tree = JsonUtils.jsonTree(gamer);
		query.updateOne("uniqueId", gamer.getUniqueId().toString(),
				new JsonBuilder().addProperty("fieldName", fieldName).add("value", tree.get(fieldName)).build());
	}

	public static Query<JsonElement> createDefault(MongoConnection mongoConnection) {
		return new MongoQuery(mongoConnection, mongoConnection.getDataBase(), "members-hg");
	}

}
