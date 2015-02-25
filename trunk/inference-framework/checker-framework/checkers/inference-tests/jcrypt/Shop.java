import java.util.Map;
import java.util.LinkedHashMap;

public class Shop {

	private Map quantityMap;

	/**
	 * Maps ItemIDs to "Add to cart" Buttons.
	 */
	public Shop() {
		quantityMap = new LinkedHashMap();
	}

	public void refreshInventory() {
		Map inventory = new LinkedHashMap();
		quantityMap.keySet().retainAll(inventory.keySet());
		inventory = null;
	}

}
