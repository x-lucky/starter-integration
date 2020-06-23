package cn.xlucky.framework.rabbitmq.listener;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;


/**
 * RabbitMq监听基类
 *
 * @author xlucky
 * @version 1.0.0 2020年6月22日
 */
public abstract class BaseMqListener {

	private static Map<String, Integer> records = new HashMap<>();

	/**
	 * 获取消息
	 * <p>
	 *
	 * @param  message
	 * @return
	 * @author xlucky
	 * @date 2020/6/22 16:55
	 * @version 1.0.0
	 */
	public String gainData(Message message) throws Exception {

		String receiveMsg = null;

		MessageProperties messageProperties = message.getMessageProperties();
		String encoding = "UTF-8";
		if (messageProperties != null) {
			if (StringUtils.isNotEmpty(messageProperties.getContentEncoding())) {
				encoding = messageProperties.getContentEncoding();
			}
		}
		receiveMsg = new String(message.getBody(), encoding);
		return receiveMsg;
	}

	/**
	 * 获取消息 T
	 * <p>
	 *
	 * @param  message
	 * @param  valueType
	 * @return
	 * @author xlucky
	 * @date 2020/6/22 16:55
	 * @version 1.0.0
	 */
	public <T> T gainData(Message message, Class<T> valueType) throws Exception {
		T dto = null;
		String receiveMsg = gainData(message);
		dto = JSON.parseObject(receiveMsg, valueType);
		return dto;
	}

	/**
	 * 增加失败次数
	 * <p>
	 *
	 * @param queue
	 * @param key
	 * @return
	 * @author xlucky
	 * @date 2020/6/22 17:30
	 * @version 1.0.0
	 */
	public void addErrorCount(String queue, String key) {
		key = queue + "_" + key;
		if (records.containsKey(key)) {
			records.put(key, records.get(key) + 1);
		} else {
			records.put(key, 1);
		}
	}


	/**
	 * 获取失败次数
	 * <p>
	 *
	 * @param queue
	 * @param key
	 * @return
	 * @author xlucky
	 * @date 2020/6/22 17:30
	 * @version 1.0.0
	 */
	public int getErrorCount(String queue, String key) {
		key = queue + "_" + key;
		if (records.containsKey(key)) {
			return records.get(key);
		} else {
			return 0;
		}
	}

	/**
	 * 清空失败次数
	 * <p>
	 *
	 * @param  * @param null
	 * @return
	 * @author xlucky
	 * @date 2020/6/22 17:30
	 * @version 1.0.0
	 */
	public void clearErrorCount(String queue, String key) {
		key = queue + "_" + key;
		records.remove(key);
	}

}