package gsshop.mobile.v2;

import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.shop.order.OrderItem;
import gsshop.mobile.v2.home.shop.order.ShopOrderSettingDialogFragment;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(HomeActivity.class);

    String[] names = {"aaaa", "bbbb", "cccc", "dddd", "eeee", "ffff", "iiii", "jjjj", "kkkk", "llll", "mmmm", "nnnn", "oooo", "pppp", "qqqq", "rrrr", "ssss", "tttt", "uuuu", "vvvv", "wwww", "xxxx", "yyyy", "zzzz"};
    @Test
    public void testShopOrder() throws InterruptedException {
        List<OrderItem> items = new ArrayList();
        OrderItem item;
        for(String name: names) {
            item = new OrderItem();
            item.shopName = name;
            items.add(item);
        }

        String json = new Gson().toJson(items);
        ShopOrderSettingDialogFragment dialog = ShopOrderSettingDialogFragment.newInstance(json);
        dialog.show(mActivityRule.getActivity().getSupportFragmentManager(), ShopOrderSettingDialogFragment.class.getSimpleName());
        while(!mActivityRule.getActivity().isFinishing()) {
            Thread.sleep(2400);
        }
    }
}
