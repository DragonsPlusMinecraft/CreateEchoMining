package plus.dragons.createminingindustry.entry;

import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import plus.dragons.createminingindustry.MiningIndustry;

public class CmiContainerTypes {

    /*public static final MenuEntry<EnchantingGuideMenu> ENCHANTING_GUIDE_FOR_BLAZE =
            register("enchanting_guide_for_blaze", EnchantingGuideMenu::new, () -> EnchantingGuideScreen::new);*/

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
            String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return MiningIndustry.registrate()
                .menu(name, factory, screenFactory)
                .register();
    }

    public static void register() {
    }
}
