#import <Cordova/CDVPlugin.h>

@interface CDVCookieEmperor : CDVPlugin

- (void)getCookieValue:(CDVInvokedUrlCommand*)command;
- (void)getAllCookies:(CDVInvokedUrlCommand*)command;
- (void)setCookieValue:(CDVInvokedUrlCommand*)command;
- (void)clearCookies:(CDVInvokedUrlCommand*)command;

@end
