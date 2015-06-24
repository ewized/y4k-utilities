/*
 * Copyright 2015 Year4000.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package net.year4000.utilities.bungee.locale;

import net.year4000.utilities.bungee.Utilities;
import net.year4000.utilities.locale.ClassLocaleManager;

class ClassMessageManager extends ClassLocaleManager {
    private static ClassMessageManager inst;

    private ClassMessageManager() {
        super(Utilities.getInst().getLog(), "/net/year4000/utilities/locales/", "en_US", "en_PT", "pt_PT", "pt_BR");
    }

    static ClassMessageManager get() {
        if (inst == null) {
            inst = new ClassMessageManager();
        }

        return inst;
    }
}
