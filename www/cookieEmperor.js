/**
 * CookieEmperor
 * @constructor
 */
function CookieEmperor(pluginName) {
    this.version = '1.1.0';
    this.pluginName = pluginName;
}

/**
 * sets callback for when the custom webview loads a resource
 * @param url
 * @param successCallback
 * @param errorCallback
 */
CookieEmperor.prototype.onLoadResource = function (filter, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, this.pluginName, 'onLoadResource', [filter]);
};

/**
 * returns all cookie values
 * @param url
 * @param successCallback
 * @param errorCallback
 */
CookieEmperor.prototype.getAllCookies = function(url, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, this.pluginName, 'getAllCookies', [url]);
};

/**
 * returns cookie value
 * @param url
 * @param cookieName
 * @param successCallback
 * @param errorCallback
 */
CookieEmperor.prototype.getCookie = function(url, cookieName, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, this.pluginName, 'getCookieValue', [url, cookieName]);
};

/**
 * sets cookie
 * @param url
 * @param cookieName
 * @param cookieValue
 * @param successCallback
 * @param errorCallback
 */
CookieEmperor.prototype.setCookie = function (url, cookieName, cookieValue, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback,  this.pluginName, 'setCookieValue', [url, cookieName, cookieValue]);
};

/**
 * set more than one cookie
 * @param url
 * @param cookieName
 * @param cookieValue
 * @param successCallback
 * @param errorCallback
 */
CookieEmperor.prototype.setCookieMulti = function (url, cookie, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback,  this.pluginName, 'setCookieMulti', [url, cookie]);
};

/**
 * clears all cookies
 * @param successCallback
 * @param errorCallback
 */
CookieEmperor.prototype.clearAll = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback,  this.pluginName, 'clearCookies', []);
};

/**
 * export default CookieEmperor
 * @type {CookieEmperor}
 */
module.exports = new CookieEmperor('CookieEmperor');
