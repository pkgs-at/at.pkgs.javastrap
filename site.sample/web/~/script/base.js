(function(root, unasigned) {
	var page;

	root.page = page = root.page || new Object();
	page.namespace = function(name) {
		var node;

		node = page;
		name.split(/\./).each(function(name) {
			node = (node[name] = node[name] || new Object());
		});
		return node;
	};
	page.classify = function(callee, properties) {
		var proxy;

		proxy = function() {
			return callee.apply(proxy, arguments);
		};
		if (properties) Object.keys(properties, function(key, value) {
			proxy[key] = value;
		});
		return proxy;
	};
	page.Option = function(defaults) {
		this.defaults = defaults;
	};
	page.Option.prototype.merge = function(option, mixin) {
		var values;

		values = new Object();
		if (this.defaults) Object.keys(this.defaults, function(key, value) {
			values[key] = value;
		});
		Object.keys(Object.isString(option) ? eval('({' + option + '})') : option, function(key, value) {
			values[key] = value;
		});
		if (mixin) Object.keys(mixin, function(key, value) {
			values[key] = value;
		});
		return values;
	};
	page.getPreflightToken = page.classify(function(option) {
		var token;

		token = null;
		$.ajax(this.option.merge(option, {
			success: function(data) {
				token = data;
			}
		}));
		return token;
	}, {
		option: new page.Option({
			async: false,
			cache: false,
			dataType: 'text',
			type: 'POST'
		})
	});
	page.initialize = page.classify(function($container) {
		Object.keys(this.attributes, function(attribute, binder) {
			$container.find('[' + attribute + ']').each(function() {
				var $this;

				$this = $(this);
				binder($this, $this.attr(attribute));
			});
		});
	}, {
		attributes: new Object(),
		addAttributeBinder: function(name, binder) {
			this.attributes[name] = binder;
		},
	});
	page.initialize.addAttributeBinder('data-form-preflight-token', function($element, value) {
		var lock;

		lock = false;
		$element.closest('form').submit(function() {
			var token;

			if (lock) return false;
			lock = true;
			token = page.getPreflightToken(value);
			if (!Object.isString(token)) {
				lock = false;
				return false;
			}
			$element.val(token);
			return true;
		});
	});
	page.initialize.addAttributeBinder('data-form-binding', function($element, value) {
		var option;
		var $source;
		var $destination;

		option = new page.Option().merge(value);
		if (Object.has(option, 'destination')) {
			$destination = $element.find(option.destination.replace(/`/gm, '"'));
			$element.submit(function() {
				$destination.val($element.at().store(true));
				return true;
			});
		}
	});
	$(function() {
		page.initialize($('body'));
	});
})(this);
