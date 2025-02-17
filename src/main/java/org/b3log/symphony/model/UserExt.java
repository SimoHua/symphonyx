/*
 * Copyright (c) 2012-2016, b3log.org & hacpai.com & fangstar.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.symphony.model;

/**
 * This class defines ext of user model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 3.17.1.9, Mar 8, 2016
 * @since 0.2.0
 * @see org.b3log.latke.model.User
 */
public final class UserExt {

    /**
     * Key of user real name.
     */
    public static final String USER_REAL_NAME = "userRealName";

    /**
     * Key of user geo status.
     */
    public static final String USER_GEO_STATUS = "userGeoStatus";

    /**
     * Key of user update time.
     */
    public static final String USER_UPDATE_TIME = "userUpdateTime";

    /**
     * Key of user city.
     */
    public static final String USER_CITY = "userCity";

    /**
     * Key of user country.
     */
    public static final String USER_COUNTRY = "userCountry";

    /**
     * Key of user province.
     */
    public static final String USER_PROVINCE = "userProvince";

    /**
     * Key of user skin.
     */
    public static final String USER_SKIN = "userSkin";

    /**
     * Key of user longest checkin streak start.
     */
    public static final String USER_LONGEST_CHECKIN_STREAK_START = "userLongestCheckinStreakStart";

    /**
     * Key of user longest checkin streak end.
     */
    public static final String USER_LONGEST_CHECKIN_STREAK_END = "userLongestCheckinStreakEnd";

    /**
     * Key of user current checkin streak start.
     */
    public static final String USER_CURRENT_CHECKIN_STREAK_START = "userCurrentCheckinStreakStart";

    /**
     * Key of user current checkin streak start end.
     */
    public static final String USER_CURRENT_CHECKIN_STREAK_END = "userCurrentCheckinStreakEnd";

    /**
     * Key of user longest checkin streak.
     */
    public static final String USER_LONGEST_CHECKIN_STREAK = "userLongestCheckinStreak";

    /**
     * Key of user current checkin streak.
     */
    public static final String USER_CURRENT_CHECKIN_STREAK = "userCurrentCheckinStreak";

    /**
     * Key of user article count.
     */
    public static final String USER_ARTICLE_COUNT = "userArticleCount";

    /**
     * Key of user comment count.
     */
    public static final String USER_COMMENT_COUNT = "userCommentCount";

    /**
     * Key of new tag count.
     */
    public static final String USER_TAG_COUNT = "userTagCount";

    /**
     * Key of user status.
     */
    public static final String USER_STATUS = "userStatus";

    /**
     * Key of user point.
     */
    public static final String USER_POINT = "userPoint";

    /**
     * Key of user used point.
     */
    public static final String USER_USED_POINT = "userUsedPoint";

    /**
     * Key of user join point rank.
     */
    public static final String USER_JOIN_POINT_RANK = "userJoinPointRank";

    /**
     * Key of user join used point rank.
     */
    public static final String USER_JOIN_USED_POINT_RANK = "userJoinUsedPointRank";

    /**
     * Key of user tags.
     */
    public static final String USER_TAGS = "userTags";

    /**
     * Key of user QQ.
     */
    public static final String USER_QQ = "userQQ";

    /**
     * Key of user number.
     */
    public static final String USER_NO = "userNo";

    /**
     * Key of user intro.
     */
    public static final String USER_INTRO = "userIntro";

    /**
     * Key of user avatar type.
     */
    public static final String USER_AVATAR_TYPE = "userAvatarType";

    /**
     * Key of user avatar URL.
     */
    public static final String USER_AVATAR_URL = "userAvatarURL";

    /**
     * Key of online flag.
     */
    public static final String USER_ONLINE_FLAG = "userOnlineFlag";

    /**
     * Key of latest post article time.
     */
    public static final String USER_LATEST_ARTICLE_TIME = "userLatestArticleTime";

    /**
     * Key of latest comment time.
     */
    public static final String USER_LATEST_CMT_TIME = "userLatestCmtTime";

    /**
     * Key of latest login time.
     */
    public static final String USER_LATEST_LOGIN_TIME = "userLatestLoginTime";

    /**
     * Key of latest login IP.
     */
    public static final String USER_LATEST_LOGIN_IP = "userLatestLoginIP";

    /**
     * Key of app role.
     */
    public static final String USER_APP_ROLE = "userAppRole";

    /**
     * Key of team.
     */
    public static final String USER_TEAM = "userTeam";

    //// Transient ////
    /**
     * Key of user create time.
     */
    public static final String USER_T_CREATE_TIME = "userCreateTime";

    /**
     * Key of user point in Hex.
     */
    public static final String USER_T_POINT_HEX = "userPointHex";

    /**
     * Key of user point in Color Code.
     */
    public static final String USER_T_POINT_CC = "userPointCC";

    //// Null user
    /**
     * Null user name.
     */
    public static final String NULL_USER_NAME = "_";

    //// Status constants
    /**
     * User status - valid.
     */
    public static final int USER_STATUS_C_VALID = 0;

    /**
     * User status - invalid.
     */
    public static final int USER_STATUS_C_INVALID = 1;

    /**
     * User status - registered but not verified.
     */
    public static final int USER_STATUS_C_NOT_VERIFIED = 2;

    //// Join point rank constants
    /**
     * User join point rank - join.
     */
    public static final int USER_JOIN_POINT_RANK_C_JOIN = 0;

    /**
     * User join point rank - not join.
     */
    public static final int USER_JOIN_POINT_RANK_C_NOT_JOIN = 1;

    /**
     * User join used point rank - join.
     */
    public static final int USER_JOIN_USED_POINT_RANK_C_JOIN = 0;

    /**
     * User join used point rank - not join.
     */
    public static final int USER_JOIN_USED_POINT_RANK_C_NOT_JOIN = 1;

    //// Geo Status constants
    /**
     * User geo status - public.
     */
    public static final int USER_GEO_STATUS_C_PUBLIC = 0;

    /**
     * User geo status - private.
     */
    public static final int USER_GEO_STATUS_C_PRIVATE = 1;

    //// Avatar type constants
    /**
     * User avatar type - Upload.
     */
    public static final int USER_AVATAR_TYPE_C_UPLOAD = 2;

    //// User role constants
    /**
     * Mall admin role.
     */
    public static final String MALL_ADMIN_ROLE = "mallAdminRole";

    //// App role constants
    /**
     * User app role - Hacker.
     */
    public static final int USER_APP_ROLE_C_HACKER = 0;

    /**
     * User app role - Painter.
     */
    public static final int USER_APP_ROLE_C_PAINTER = 1;

    /**
     * Gets color code of the specified point.
     *
     * @param point the specified point
     * @return color code
     */
    public static String toCCString(final int point) {
        final String hex = Integer.toHexString(point);

        if (1 == hex.length()) {
            return hex + hex + hex + hex + hex + hex;
        }

        if (2 == hex.length()) {
            final String a1 = hex.substring(0, 1);
            final String a2 = hex.substring(1);

            return a1 + a1 + a1 + a2 + a2 + a2;
        }

        if (3 == hex.length()) {
            final String a1 = hex.substring(0, 1);
            final String a2 = hex.substring(1, 2);
            final String a3 = hex.substring(2);

            return a1 + a1 + a2 + a2 + a3 + a3;
        }

        if (4 == hex.length()) {
            final String a1 = hex.substring(0, 1);
            final String a2 = hex.substring(1, 2);
            final String a3 = hex.substring(2, 3);
            final String a4 = hex.substring(3);

            return a1 + a2 + a3 + a4 + a3 + a4;
        }

        if (5 == hex.length()) {
            final String a1 = hex.substring(0, 1);
            final String a2 = hex.substring(1, 2);
            final String a3 = hex.substring(2, 3);
            final String a4 = hex.substring(3, 4);
            final String a5 = hex.substring(4);

            return a1 + a2 + a3 + a4 + a5 + a5;
        }

        if (6 == hex.length()) {
            return hex;
        }

        return hex.substring(0, 6);
    }

    /**
     * Private constructor.
     */
    private UserExt() {
    }
}
