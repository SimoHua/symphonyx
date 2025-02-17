<#include "../macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${addArticleLabel} - ${symphonyLabel}">
        <meta name="robots" content="none" />
        </@head>
        <link type="text/css" rel="stylesheet" href="${staticServePath}/css/home${miniPostfix}.css?${staticResourceVersion}" />
        <link rel="stylesheet" href="${staticServePath}/js/lib/editor/codemirror.css">
        <link type="text/css" rel="stylesheet" href="${staticServePath}/js/lib/highlight.js-8.6/styles/github.css">
    </head>
    <body>
        <#include "../header.ftl">
        <div class="main">
            <div class="wrapper post">
                <div class="form fn-flex-1 fn-clear">
                    <#if !articleType??>
                    <#assign articleType=article.articleType>
                    </#if>
                    <#if 0 == articleType>
                        ${articleLabel} - <span class="ft-gray"><span class="ft-green">提问</span>或<span class="ft-green">分享</span>对别人有帮助的经验与见解</span>
                    <#elseif 1 == articleType>
                        ${discussionLabel} - <span class="ft-gray">@好友并在<span class="ft-red">私密</span>空间中进行交流</span>
                    <#elseif 4 == articleType>
                        ${journalLabel} - <span class="ft-gray"><span class="ft-green">记录</span>下今天的航海冒险经历</span>
                    <#elseif 3 == articleType>
                        ${thoughtLabel} - 
                        <span class="ft-gray">
                            写作过程的记录与重放，文字版的<span class="ft-red">沙画</span>表演
                            <a href="https://hacpai.com/article/1441942422856" target="_blank">(?)</a>
                        </span>
                    </#if>
                    <div>
                        <br/>
                        <input type="text" id="articleTitle" tabindex="1"
                               value="<#if article??>${article.articleTitle}<#else><#if 4 == articleType>${articleTitle}</#if></#if>" placeholder="${titleLabel}" />
                    </div>
                    <div class="fn-clear">
                        <label class="article-content-label">
                            Markdown
                            <a href="javascript:AddArticle.grammar()">${baseGrammarLabel}</a>
                            <a target="_blank" href="http://daringfireball.net/projects/markdown/syntax">${allGrammarLabel}</a>
                            |
                            <a target="_blank" href="${servePath}/emoji/index.html">Emoji</a>
                        </label>
                    </div>
                    <div class="fn-clear article-content">
                        <textarea id="articleContent" tabindex="2"
                                  placeholder="<#if !article?? && 1 == articleType>${addDiscussionEditorPlaceholderLabel}</#if>${addArticleEditorPlaceholderLabel}"><#if article??>${article.articleContent}</#if><#if at??>@${at}</#if></textarea>
                        <div class="fn-left grammar fn-none">
                            ${markdwonGrammarLabel}
                        </div>
                    </div>
                    <div>
                        <input id="articleTags" type="text" tabindex="3" 
                               value="<#if article??>${article.articleTags}<#else>${tags}</#if>" placeholder="${tagLabel}（${tagSeparatorTipLabel}）"/>
                        <br/><br/>
                    </div>
                    <div class="fn-clear article-reward-content">
                        <textarea id="articleRewardContent" tabindex="4"
                                  placeholder="${rewardEditorPlaceholderLabel}"><#if article??>${article.articleRewardContent}</#if></textarea>
                    </div>
                    <div>
                        <input id="articleRewardPoint" type="number" tabindex="5" 
                               value="<#if article?? && 0 != article.articleRewardPoint>${article.articleRewardPoint}</#if>" placeholder="${rewardPointLabel}" <#if article?? && 0 < article.articleRewardPoint>readonly="readonly"</#if>/>
                               <br/><br/>
                    </div>
                    <div class="tip" id="addArticleTip"></div>
                    <div class="fn-clear fn-none">
                        <#if !article??>
                        <label> &nbsp;
                            <input type="radio" name="articleType" <#if 0 == articleType>checked="checked"</#if> value="0"/> 
                                   ${articleLabel}
                        </label>
                        <label id="articleType3"> &nbsp;
                            <input type="radio" name="articleType" <#if 3 == articleType>checked="checked"</#if> value="3"/> 
                                   ${thoughtLabel}
                        </label>
                        <label> &nbsp;
                            <input type="radio" name="articleType" <#if 1 == articleType>checked="checked"</#if> value="1"/> 
                                   ${discussionLabel}
                        </label>
                        <label> &nbsp;
                            <input type="radio" name="articleType" <#if 4 == articleType>checked="checked"</#if> value="4"/> 
                                   ${journalLabel}
                        </label>
                        <#else>
                        <input class="fn-none" type="radio" name="articleType" value="${article.articleType}" checked="checked"/> 
                        </#if>
                    </div>
                    <div class="fn-right"><br/>
                        <button class="red" tabindex="10" onclick="AddArticle.add(<#if article??>'${article.oId}'<#else>null</#if>,'${csrfToken}')"><#if article??>${submitLabel}<#else>${postLabel}</#if></button>
                    </div>
                </div>
            </div>
        </div>
        <#include "../footer.ftl">
        <script src="${staticServePath}/js/lib/jquery/jquery.bowknot.min.js"></script>
        <script src="${staticServePath}/js/lib/editor/codemirror.min.js?4.13"></script>
        <script src="${staticServePath}/js/lib/editor/editor.js"></script>
        <script type="text/javascript" src="${staticServePath}/js/lib/highlight.js-8.6/highlight.pack.js"></script>
        <script type="text/javascript" src="${staticServePath}/js/lib/jquery/file-upload-9.10.1/jquery.fileupload.min.js"></script>
        <script type="text/javascript" src="${staticServePath}/js/lib/sound-recorder/SoundRecorder.js"></script>
        <script>
                        Label.articleTitleErrorLabel = "${articleTitleErrorLabel}";
                        Label.articleContentErrorLabel = "${articleContentErrorLabel}";
                        Label.tagsErrorLabel = "${tagsErrorLabel}";
                        Label.userName = "${userName}";
                        Label.recordDeniedLabel = "${recordDeniedLabel}";
                        Label.recordDeviceNotFoundLabel = "${recordDeviceNotFoundLabel}";
                        Label.uploadLabel = "${uploadLabel}";
        </script>
        <script src="${staticServePath}/js/add-article${miniPostfix}.js?${staticResourceVersion}"></script>
        <script type="text/javascript" src="${staticServePath}/js/audio${miniPostfix}.js?${staticResourceVersion}"></script>
        <script>
                        Util.uploadFile({
                        "id": "fileUpload",
                                "pasteZone": $("#articleContent").next().next(),
                                "qiniuUploadToken": "${qiniuUploadToken}",
                                "editor": AddArticle.editor,
                                "uploadingLabel": "${uploadingLabel}",
                                "qiniuDomain": "${qiniuDomain}"
                        });
                        Util.uploadFile({
                        "id": "rewardFileUpload",
                                "pasteZone": $("#articleRewardContent").next().next(),
                                "qiniuUploadToken": "${qiniuUploadToken}",
                                "editor": AddArticle.rewardEditor,
                                "uploadingLabel": "${uploadingLabel}",
                                "qiniuDomain": "${qiniuDomain}"
                        });
                        var qiniuToken = '${qiniuUploadToken}';
                        var qiniuDomain = '${qiniuDomain}';
                        var audioRecordingLabel = '${audioRecordingLabel}';
                        var uploadingLabel = '${uploadingLabel}';
        </script>
    </body>
</html>
