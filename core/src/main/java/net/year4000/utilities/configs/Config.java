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

package net.year4000.utilities.configs;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.EqualsAndHashCode;
import net.year4000.utilities.sdk.HttpConnection;
import net.year4000.utilities.sdk.HttpFetcher;
import org.eclipse.jetty.io.RuntimeIOException;

/**
 * Make it simple for a class to constructor the object from a JSON web page.
 */
@EqualsAndHashCode
public abstract class Config {
    private LoadingCache<Class<? extends Config>, Config> cache;

    /** Get the class object */
    public static <T extends Config> T getInstance(Config self) {
        final ConfigURL url = Preconditions.checkNotNull(self.getClass().getAnnotation(ConfigURL.class));

        if (self.cache == null) {
            self.cache = CacheBuilder.<Class<? extends Config>, Config>newBuilder()
                .expireAfterWrite(
                    url.expire(),
                    url.unit()
                )
                .build(new CacheLoader<Class<? extends Config>, Config>() {
                    @Override
                    public Config load(Class<? extends Config> clazz) throws Exception {
                        HttpConnection connection = new HttpConnection(url.value());

                        try {
                            return HttpFetcher.get(connection, clazz);
                        }
                        catch (Exception e) {
                            System.err.println(e.toString());
                            throw new RuntimeIOException(e);
                        }
                    }
                });
        }

        // Have to cast or wont compile right
        return (T) self.cache.getUnchecked(url.config());
    }
}
