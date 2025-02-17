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
package org.b3log.symphony.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.apache.commons.lang.time.DateUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.CompositeFilter;
import org.b3log.latke.repository.CompositeFilterOperator;
import org.b3log.latke.repository.Filter;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.symphony.model.Archive;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.repository.ArchiveRepository;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.util.Emotions;
import org.b3log.symphony.util.Symphonys;
import org.b3log.symphony.util.Times;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Journal query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.3.8, Feb 20, 2016
 * @since 1.4.0
 */
@Service
public class JournalQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JournalQueryService.class.getName());

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Article query service.
     */
    @Inject
    private ArticleQueryService articleQueryService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Short link query service.
     */
    @Inject
    private ShortLinkQueryService shortLinkQueryService;

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Archive repository.
     */
    @Inject
    private ArchiveRepository archiveRepository;

    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * Gets the recent (sort by create time) journals with the specified fetch size. The first one is section, the
     * followings are chapters.
     *
     * @param currentPageNum the specified current page number
     * @param fetchSize the specified fetch size
     * @return recent articles, returns an empty list if not found
     * @throws ServiceException service exception
     */
    public List<JSONObject> getRecentJournals(final int currentPageNum, final int fetchSize) throws ServiceException {
        final Query chapterQuery = new Query()
                .addSort(Keys.OBJECT_ID, SortDirection.DESCENDING)
                .setPageSize(fetchSize).setCurrentPageNum(currentPageNum);

        chapterQuery.setFilter(CompositeFilterOperator.and(
                new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, Article.ARTICLE_STATUS_C_VALID),
                new PropertyFilter(Article.ARTICLE_TYPE, FilterOperator.EQUAL, Article.ARTICLE_TYPE_C_JOURNAL_CHAPTER)));

        final Query sectionQuery = new Query()
                .addSort(Keys.OBJECT_ID, SortDirection.DESCENDING)
                .setPageSize(1).setCurrentPageNum(1);

        sectionQuery.setFilter(CompositeFilterOperator.and(
                new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, Article.ARTICLE_STATUS_C_VALID),
                new PropertyFilter(Article.ARTICLE_TYPE, FilterOperator.EQUAL, Article.ARTICLE_TYPE_C_JOURNAL_SECTION)));

        try {
            final JSONObject chapterResult = articleRepository.get(chapterQuery);
            final JSONObject sectionResult = articleRepository.get(sectionQuery);

            final List<JSONObject> ret
                    = CollectionUtils.<JSONObject>jsonArrayToList(sectionResult.optJSONArray(Keys.RESULTS));
            final List<JSONObject> chapterList
                    = CollectionUtils.<JSONObject>jsonArrayToList(chapterResult.optJSONArray(Keys.RESULTS));

            ret.addAll(chapterList);

            final int pageCount = chapterResult.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_PAGE_COUNT);

            articleQueryService.organizeArticles(ret);

            for (final JSONObject article : ret) {
                article.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);

                final String authorId = article.optString(Article.ARTICLE_AUTHOR_ID);
                final JSONObject author = userRepository.get(authorId);
                if (UserExt.USER_STATUS_C_INVALID == author.optInt(UserExt.USER_STATUS)) {
                    article.put(Article.ARTICLE_TITLE, langPropsService.get("articleTitleBlockLabel"));
                }
            }

            final Integer participantsCnt = Symphonys.getInt("latestArticleParticipantsCnt");
            articleQueryService.genParticipants(ret, participantsCnt);

            return ret;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets journals failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets one day's paragraphs.
     *
     * @param time sometime in the specified day
     * @return paragraphs
     */
    public List<JSONObject> getSection(final long time) {
        try {
            final Query query = new Query().addSort(Keys.OBJECT_ID, SortDirection.ASCENDING).setCurrentPageNum(1);

            final List<Filter> filters = new ArrayList<Filter>();
            filters.add(new PropertyFilter(Article.ARTICLE_TYPE, FilterOperator.EQUAL, Article.ARTICLE_TYPE_C_JOURNAL_PARAGRAPH));

            filters.add(new PropertyFilter(Article.ARTICLE_CREATE_TIME, FilterOperator.GREATER_THAN_OR_EQUAL, Times.getDayStartTime(time)));
            filters.add(new PropertyFilter(Article.ARTICLE_CREATE_TIME, FilterOperator.LESS_THAN_OR_EQUAL, Times.getDayEndTime(time)));

            query.setFilter(new CompositeFilter(CompositeFilterOperator.AND, filters));

            final JSONObject result = articleRepository.get(query);
            final List<JSONObject> paragraphs = CollectionUtils.<JSONObject>jsonArrayToList(result.optJSONArray(Keys.RESULTS));

            final List<JSONObject> ret = new ArrayList<JSONObject>();

            final JSONObject archive = archiveRepository.getArchive(time);
            final String[] teamNames = getTeams(archive);

            for (final String teamName : teamNames) {
                final List<JSONObject> users = getUsers(ret, teamName);

                final List<JSONObject> teamMembers = getTeamMembers(archive, teamName);
                for (final JSONObject teamMember : teamMembers) {
                    teamMember.put(Common.PARAGRAPHS, (Object) new ArrayList());
                }
                users.addAll(teamMembers);

                final JSONObject team = getTeam(ret, teamName);
                team.put(Common.TOTAL, teamMembers.size());
            }

            for (final JSONObject paragraph : paragraphs) {
                String articleContent = paragraph.optString(Article.ARTICLE_CONTENT);
                final Set<String> userNames = userQueryService.getUserNames(articleContent);
                for (final String userName : userNames) {
                    articleContent = articleContent.replace('@' + userName, "@<a href='" + Latkes.getServePath()
                            + "/member/" + userName + "'>" + userName + "</a>");
                }
                articleContent = shortLinkQueryService.linkArticle(articleContent);
                articleContent = shortLinkQueryService.linkTag(articleContent);
                articleContent = Emotions.convert(articleContent);
                paragraph.put(Article.ARTICLE_CONTENT, articleContent);

                articleQueryService.markdown(paragraph);

                articleQueryService.organizeArticle(paragraph);

                final String pAuthorId = paragraph.optString(Article.ARTICLE_AUTHOR_ID);
                final JSONObject pAuthor = userRepository.get(pAuthorId);
                final String userName = pAuthor.optString(User.USER_NAME);
                final String teamName = getTeamName(archive, pAuthorId);

                final List<JSONObject> users = getUsers(ret, teamName);
                final List<JSONObject> paras = getParagraphs(users, userName);
                paragraph.put(UserExt.USER_TEAM, teamName);
                paras.add(paragraph);
            }

            articleQueryService.genParticipants(paragraphs, Symphonys.getInt("latestArticleParticipantsCnt"));

            doneCount(ret, paragraphs);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets section failed", e);

            return Collections.emptyList();
        }
    }

    /**
     * Gets one week's chapter.
     *
     * @param time sometime in the specified week
     * @return paragraphs
     */
    public List<JSONObject> getChapter(final long time) {
        try {
            final Query query = new Query().addSort(Keys.OBJECT_ID, SortDirection.ASCENDING).
                    setCurrentPageNum(1);

            final List<Filter> filters = new ArrayList<Filter>();
            filters.add(new PropertyFilter(Article.ARTICLE_TYPE, FilterOperator.EQUAL, Article.ARTICLE_TYPE_C_JOURNAL_PARAGRAPH));

            filters.add(new PropertyFilter(Article.ARTICLE_CREATE_TIME, FilterOperator.GREATER_THAN_OR_EQUAL, Times.getWeekStartTime(time)));
            filters.add(new PropertyFilter(Article.ARTICLE_CREATE_TIME, FilterOperator.LESS_THAN_OR_EQUAL, Times.getWeekEndTime(time)));

            query.setFilter(new CompositeFilter(CompositeFilterOperator.AND, filters));

            final JSONObject result = articleRepository.get(query);
            final List<JSONObject> paragraphs = CollectionUtils.<JSONObject>jsonArrayToList(result.optJSONArray(Keys.RESULTS));

            final List<JSONObject> ret = new ArrayList<JSONObject>();

            final JSONObject archive = archiveRepository.getWeekArchive(time);
            final String[] teamNames = getTeams(archive);

            for (final String teamName : teamNames) {
                final List<JSONObject> users = getUsers(ret, teamName);

                final List<JSONObject> teamMembers = getTeamMembers(archive, teamName);

                for (final JSONObject teamMember : teamMembers) {
                    int day = 1;
                    final List<JSONObject> weekDays = new ArrayList<JSONObject>();

                    final long now = System.currentTimeMillis();
                    if (now > Times.getWeekEndTime(time)) {
                        day = 7;
                    } else {
                        day = Times.getWeekDay(now);
                    }

                    for (int i = 1; i <= day; i++) {
                        final JSONObject d = new JSONObject();
                        d.put(Common.WEEK_DAY, i);
                        d.put(Common.WEEK_DAY_NAME, Times.getWeekDayName(i));
                        weekDays.add(d);
                    }

                    for (final JSONObject weekDay : weekDays) {
                        weekDay.put(Common.PARAGRAPHS, (Object) new ArrayList<JSONObject>());
                    }

                    teamMember.put(Common.WEEK_DAYS, (Object) weekDays);

                }

                users.addAll(teamMembers);

                final JSONObject team = getTeam(ret, teamName);
                team.put(Common.TOTAL, teamMembers.size());
            }

            for (final JSONObject paragraph : paragraphs) {
                String articleContent = paragraph.optString(Article.ARTICLE_CONTENT);
                final Set<String> userNames = userQueryService.getUserNames(articleContent);
                for (final String userName : userNames) {
                    articleContent = articleContent.replace('@' + userName, "@<a href='" + Latkes.getServePath()
                            + "/member/" + userName + "'>" + userName + "</a>");
                }
                articleContent = shortLinkQueryService.linkArticle(articleContent);
                articleContent = shortLinkQueryService.linkTag(articleContent);
                articleContent = Emotions.convert(articleContent);
                paragraph.put(Article.ARTICLE_CONTENT, articleContent);

                articleQueryService.markdown(paragraph);

                articleQueryService.organizeArticle(paragraph);

                final String pAuthorId = paragraph.optString(Article.ARTICLE_AUTHOR_ID);
                final JSONObject pAuthor = userRepository.get(pAuthorId);
                final String userName = pAuthor.optString(User.USER_NAME);
                final String teamName = getTeamName(archive, pAuthorId);

                final List<JSONObject> users = getUsers(ret, teamName);
                final List<JSONObject> weekDays = getWeekDays(users, userName, time);

                final long created = paragraph.optLong(Keys.OBJECT_ID);
                final int day = Times.getWeekDay(created);

                final List<JSONObject> paras = getWeekDayParagraphs(weekDays, day);
                paras.add(paragraph);
            }

            articleQueryService.genParticipants(paragraphs, Symphonys.getInt("latestArticleParticipantsCnt"));

            userWeekDoneCount(ret);
            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets chapter failed", e);

            return Collections.emptyList();
        }
    }

    /**
     * Section generated today?
     *
     * @return {@code true} if section generated, returns {@code false} otherwise
     */
    public synchronized boolean hasSectionToday() {
        try {
            final Query query = new Query().addSort(Keys.OBJECT_ID, SortDirection.DESCENDING).
                    setCurrentPageNum(1).setPageSize(1);

            query.setFilter(new PropertyFilter(Article.ARTICLE_TYPE, FilterOperator.EQUAL,
                    Article.ARTICLE_TYPE_C_JOURNAL_SECTION));

            final JSONObject result = articleRepository.get(query);
            final List<JSONObject> journals = CollectionUtils.<JSONObject>jsonArrayToList(result.optJSONArray(Keys.RESULTS));

            if (journals.isEmpty()) {
                return false;
            }

            final JSONObject maybeToday = journals.get(0);
            final long created = maybeToday.optLong(Article.ARTICLE_CREATE_TIME);

            return DateUtils.isSameDay(new Date(created), new Date());
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Check section generated failed", e);

            return false;
        }
    }

    /**
     * Chapter generated this week?
     *
     * @return {@code true} if chapter generated, returns {@code false} otherwise
     */
    public synchronized boolean hasChapterWeek() {
        try {
            final Query query = new Query().addSort(Keys.OBJECT_ID, SortDirection.DESCENDING).
                    setCurrentPageNum(1).setPageSize(1);

            query.setFilter(new PropertyFilter(Article.ARTICLE_TYPE, FilterOperator.EQUAL,
                    Article.ARTICLE_TYPE_C_JOURNAL_CHAPTER));

            final JSONObject result = articleRepository.get(query);
            final List<JSONObject> journals = CollectionUtils.<JSONObject>jsonArrayToList(result.optJSONArray(Keys.RESULTS));

            if (journals.isEmpty()) {
                return false;
            }

            final JSONObject maybeToday = journals.get(0);
            final long created = maybeToday.optLong(Article.ARTICLE_CREATE_TIME);

            return Times.isSameWeek(new Date(created), new Date());
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Check chapter generated failed", e);

            return false;
        }
    }

    /**
     * Checks the user specified by the given user id has post journal paragraph or not.
     *
     * @param userId the given user id
     * @return {@code true} if post, returns {@code false} otherwise
     */
    public boolean hasPostParagraphToday(final String userId) {
        final long now = System.currentTimeMillis();

        try {
            final Query query = new Query().addSort(Keys.OBJECT_ID, SortDirection.DESCENDING).
                    setCurrentPageNum(1).setPageSize(2).setFilter(CompositeFilterOperator.and(
                    new PropertyFilter(Article.ARTICLE_TYPE, FilterOperator.EQUAL, Article.ARTICLE_TYPE_C_JOURNAL_PARAGRAPH),
                    new PropertyFilter(Article.ARTICLE_AUTHOR_ID, FilterOperator.EQUAL, userId),
                    new PropertyFilter(Article.ARTICLE_CREATE_TIME, FilterOperator.GREATER_THAN_OR_EQUAL, Times.getDayStartTime(now)),
                    new PropertyFilter(Article.ARTICLE_CREATE_TIME, FilterOperator.LESS_THAN_OR_EQUAL, Times.getDayEndTime(now))));

            final JSONObject result = articleRepository.get(query);
            final List<JSONObject> journals = CollectionUtils.<JSONObject>jsonArrayToList(result.optJSONArray(Keys.RESULTS));

            return journals.size() > 1;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Check today paragraph post failed", e);

            return true;
        }
    }

    private void doneCount(final List<JSONObject> teams, final List<JSONObject> paragraphs) {
        final List<JSONObject> paras = filterByUniqueAuthor(paragraphs);

        for (final JSONObject team : teams) {
            final String teamName = team.optString(Common.TEAM_NAME);
            team.put(Common.DONE, countDone(paras, teamName));
        }
    }

    private void userWeekDoneCount(final List<JSONObject> teams) {
        for (final JSONObject team : teams) {
            int teamDoneCount = 0;

            final List<JSONObject> users = (List<JSONObject>) team.opt(User.USERS);

            for (final JSONObject user : users) {
                int userDoneCount = 0;

                for (final JSONObject day : (List<JSONObject>) user.opt(Common.WEEK_DAYS)) {
                    final List<JSONObject> paras = (List<JSONObject>) day.opt(Common.PARAGRAPHS);
                    if (!paras.isEmpty()) {
                        userDoneCount++;
                    }
                }

                user.put(Common.DONE, userDoneCount);

                if (userDoneCount > 0) {
                    teamDoneCount += userDoneCount;
                }
            }

            team.put(Common.DONE, teamDoneCount);
            team.put(Common.TOTAL, users.size() * 7);
        }
    }

    private int countDone(final List<JSONObject> paragraphs, final String teamName) {
        int ret = 0;

        for (final JSONObject paragraph : paragraphs) {
            if (paragraph.optString(UserExt.USER_TEAM).equals(teamName)) {
                ret++;
            }
        }

        return ret;
    }

    private List<JSONObject> filterByUniqueAuthor(final List<JSONObject> paragraphs) {
        final List<JSONObject> ret = new ArrayList<JSONObject>();

        for (final JSONObject paragraph : paragraphs) {
            if (!containsAuthor(ret, paragraph.optString(Article.ARTICLE_AUTHOR_ID))) {
                ret.add(paragraph);
            }
        }

        return ret;
    }

    private boolean containsAuthor(final List<JSONObject> paragraphs, final String authorId) {
        for (final JSONObject paragraph : paragraphs) {
            if (paragraph.optString(Article.ARTICLE_AUTHOR_ID).equals(authorId)) {
                return true;
            }
        }

        return false;
    }

    private List<JSONObject> getWeekDays(final List<JSONObject> users, final String userName, final long chapterTime) {
        for (final JSONObject user : users) {
            if (user.optString(User.USER_NAME).equals(userName)) {
                return (List<JSONObject>) user.opt(Common.WEEK_DAYS);
            }
        }

        return null;
    }

    private List<JSONObject> getWeekDayParagraphs(final List<JSONObject> weekDays, final int dayNum) {
        for (final JSONObject weekDay : weekDays) {
            if (weekDay.optInt(Common.WEEK_DAY) == dayNum) {
                return (List<JSONObject>) weekDay.opt(Common.PARAGRAPHS);
            }
        }

        return null;
    }

    private JSONObject getTeam(final List<JSONObject> teams, final String teamName) {
        for (final JSONObject team : teams) {
            if (team.optString(Common.TEAM_NAME).equals(teamName)) {
                return team;
            }
        }

        return null;
    }

    private String getTeamName(final JSONObject archive, final String userId) {
        try {
            final JSONArray teams = new JSONArray(archive.optString(Archive.ARCHIVE_TEAMS));

            for (int i = 0; i < teams.length(); i++) {
                final JSONObject team = teams.optJSONObject(i);
                final JSONArray users = team.optJSONArray(User.USERS);

                for (int j = 0; j < users.length(); j++) {
                    final String id = users.optString(j);

                    if (id.equals(userId)) {
                        return team.optString(Common.TEAM_NAME);
                    }
                }
            }
        } catch (final JSONException e) {
            LOGGER.log(Level.ERROR, "Gets team with archive[ " + archive + "] failed", archive);
        }

        return null;
    }

    private List<JSONObject> getUsers(final List<JSONObject> teams, final String teamName) {
        for (final JSONObject team : teams) {
            if (team.optString(Common.TEAM_NAME).equals(teamName)) {
                if (!team.has(User.USERS)) {
                    team.put(User.USERS, (Object) new ArrayList<JSONObject>());
                }

                return (List<JSONObject>) team.opt(User.USERS);
            }
        }

        final JSONObject team = new JSONObject();
        teams.add(team);
        team.put(Common.TEAM_NAME, teamName);
        team.put(User.USERS, (Object) new ArrayList<JSONObject>());

        return (List<JSONObject>) team.opt(User.USERS);
    }

    private List<JSONObject> getParagraphs(final List<JSONObject> users, final String userName) {
        for (final JSONObject user : users) {
            if (user.optString(User.USER_NAME).equals(userName)) {
                return (List<JSONObject>) user.opt(Common.PARAGRAPHS);
            }
        }

        return null;
    }

    private String[] getTeams(final JSONObject archive) {
        try {
            final JSONArray teams = new JSONArray(archive.optString(Archive.ARCHIVE_TEAMS));

            final String[] ret = new String[teams.length()];

            for (int i = 0; i < teams.length(); i++) {
                ret[i] = teams.optJSONObject(i).optString(Common.TEAM_NAME);
            }

            return ret;
        } catch (final JSONException e) {
            LOGGER.log(Level.ERROR, "Gets teams with archive[ " + archive + "] failed", archive);

            return null;
        }
    }

    private List<JSONObject> getTeamMembers(final JSONObject archive, final String teamName) {
        try {
            final JSONArray teams = new JSONArray(archive.optString(Archive.ARCHIVE_TEAMS));

            final List<JSONObject> ret = new ArrayList<JSONObject>();

            for (int i = 0; i < teams.length(); i++) {
                final JSONObject team = teams.optJSONObject(i);
                final String name = team.optString(Common.TEAM_NAME);

                if (name.equals(teamName)) {
                    final JSONArray members = team.optJSONArray(User.USERS);

                    for (int j = 0; j < members.length(); j++) {
                        final JSONObject member = new JSONObject();
                        final String userId = members.optString(j);
                        member.put(Keys.OBJECT_ID, userId);

                        final JSONObject u = userRepository.get(userId);
                        member.put(User.USER_NAME, u.optString(User.USER_NAME));
                        member.put(UserExt.USER_AVATAR_URL, u.optString(UserExt.USER_AVATAR_URL));
                        member.put(UserExt.USER_UPDATE_TIME, u.opt(UserExt.USER_UPDATE_TIME));
                        member.put(UserExt.USER_REAL_NAME, u.optString(UserExt.USER_REAL_NAME));

                        ret.add(member);
                    }

                    return ret;
                }
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets team members with archive[ " + archive + "] failed", archive);
        }

        return null;
    }
}
